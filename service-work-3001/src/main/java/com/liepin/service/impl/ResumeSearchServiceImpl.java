package com.liepin.service.impl;

import com.liepin.feign.UserInfoMicroServiceFeign;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.ResumeEducation;
import com.liepin.pojo.ResumeExpect;
import com.liepin.pojo.ResumeWorkExp;
import com.liepin.pojo.bo.SearchResumesBO;
import com.liepin.pojo.eo.SearchResumesEO;
import com.liepin.pojo.vo.ResumeVO;
import com.liepin.pojo.vo.UsersVO;
import com.liepin.service.ResumeSearchService;
import com.liepin.service.ResumeService;
import com.liepin.utils.JsonUtils;
import com.liepin.utils.LocalDateUtils;
import com.liepin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ResumeSearchServiceImpl implements ResumeSearchService {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    @Override
    public void transformAndFlush(String userId) {

        // 声明es的对象
        SearchResumesEO resumesEO = new SearchResumesEO();
        resumesEO.setUserId(userId);

        // 查询用户简历相关信息
        ResumeVO resumeVO = resumeService.getResumeInfo(userId);
        // 远程调用查询用户信息
        UsersVO usersVO = getUserInfoVO(userId);

        // 1. 把用户相关信息存入es对象中
        resumesEO.setNickname(usersVO.getNickname());
        resumesEO.setSex(usersVO.getSex());
        resumesEO.setFace(usersVO.getFace());
        resumesEO.setBirthday(LocalDateUtils.format(usersVO.getBirthday(),
                                                    LocalDateUtils.DATE_PATTERN));
        // 计算年龄
        Long age = LocalDateUtils.getChronoUnitBetween(usersVO.getBirthday(),
                                                        LocalDate.now(),
                                                        ChronoUnit.YEARS,
                                                        true);
        resumesEO.setAge(age.intValue() + 1);

        // 2. 把简历相关信息存入es对象中
        resumesEO.setResumeId(resumeVO.getId());
        resumesEO.setSkills(resumeVO.getSkills());
        resumesEO.setAdvantage(resumeVO.getAdvantage());
        resumesEO.setAdvantageHtml(resumeVO.getAdvantageHtml());
        resumesEO.setCredentials(resumeVO.getCredentials());
        resumesEO.setJobStatus(resumeVO.getStatus());
        resumesEO.setRefreshTime(LocalDateUtils.format(resumeVO.getRefreshTime(),
                                                       LocalDateUtils.DATETIME_PATTERN));
        // 计算工作年限
        Long workYears = LocalDateUtils.getChronoUnitBetween(usersVO.getStartWorkDate(),
                                                            LocalDate.now(),
                                                            ChronoUnit.YEARS,
                                                            true);
        resumesEO.setWorkYears(workYears.intValue());

        // 3. 把最新就职的企业信息存入es对象中
        List<ResumeWorkExp> workExps = resumeVO.getWorkExpList();
        // lambda表达式过滤检索日期最大的对象并且返回
        Optional<ResumeWorkExp> lastWorkOpt = workExps
                                .stream()
                                .max(Comparator.comparing(ResumeWorkExp::getBeginDate));
        ResumeWorkExp lastWork = lastWorkOpt.get();
        resumesEO.setCompanyName(lastWork.getCompanyName());
        resumesEO.setPosition(lastWork.getPosition());
        resumesEO.setIndustry(lastWork.getIndustry());

        // 4. 把最近一次教育经历存入es对象中
        List<ResumeEducation> eduList = resumeVO.getEducationList();
        Optional<ResumeEducation> lastEduOpt = eduList
                            .stream()
                            .max(Comparator.comparing(ResumeEducation::getBeginDate));
        ResumeEducation lastEdu = lastEduOpt.get();
        resumesEO.setSchool(lastEdu.getSchool());
        resumesEO.setEducation(lastEdu.getEducation());
        resumesEO.setMajor(lastEdu.getMajor());

        // 5. 获得求职期望列表，每个求职期望对应一份简历
        List<ResumeExpect> expectList = resumeService.getMyResumeExpectList(resumeVO.getId(),
                                                                            userId);
        for (ResumeExpect resumeExpect : expectList) {
            resumesEO.setResumeExpectId(resumeExpect.getId());
            resumesEO.setJobType(resumeExpect.getJobName());
            resumesEO.setCity(resumeExpect.getCity());
            resumesEO.setBeginSalary(resumeExpect.getBeginSalary());
            resumesEO.setEndSalary(resumeExpect.getEndSalary());

            IndexQuery iq = new IndexQueryBuilder().withObject(resumesEO).build();
            esTemplate.index(iq, IndexCoordinates.of("resume_result"));
        }

    }

    @Override
    public PagedGridResult searchResumesByES(SearchResumesBO searchResumesBO,
                                             Integer page,
                                             Integer pageSize) {

        System.out.println("==============================");
        System.out.println(searchResumesBO.toString());
        System.out.println("==============================");

        String basicTitle = searchResumesBO.getBasicTitle();
        String jobType = searchResumesBO.getJobType();
        Integer beginAge = searchResumesBO.getBeginAge();
        Integer endAge = searchResumesBO.getEndAge();
        Integer sex = searchResumesBO.getSex();
        Integer activeTimes = searchResumesBO.getActiveTimes();
        Integer beginWorkExpYears = searchResumesBO.getBeginWorkExpYears();
        Integer endWorkExpYears = searchResumesBO.getEndWorkExpYears();
        String edu = searchResumesBO.getEdu();
        //List<String> eduList = searchResumesBO.getEduList();
        Integer beginSalary = searchResumesBO.getBeginSalary();
        Integer endSalary = searchResumesBO.getEndSalary();
        String jobStatus = searchResumesBO.getJobStatus();

        page--;
        Pageable pageable = PageRequest.of(page, pageSize);

        // 用于区分是否需要match_all，如果包含了条件，则根据条件搜索，否则查询所有
        boolean conditionAdded = false;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 匹配默认搜索框
        if (StringUtils.isNotBlank(basicTitle)) {
            boolQueryBuilder.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("nickname", basicTitle))
                    .should(QueryBuilders.matchQuery("advantage", basicTitle))
                    .should(QueryBuilders.matchQuery("credentials", basicTitle))
                    .should(QueryBuilders.matchQuery("skills", basicTitle))
            );
            conditionAdded = true;
        }

        // 匹配职位类型
        if (StringUtils.isNotBlank(jobType)) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("jobType", jobType));
            conditionAdded = true;
        }

        // 匹配年龄
        if (beginAge > 0 && endAge >0) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("age")
                    .gte(beginAge)
                    .lte(endAge)
            );
            conditionAdded = true;
        }

        // 性别
        if (sex != null && sex != -1) {
            boolQueryBuilder.must(QueryBuilders.termQuery("sex", sex));
            conditionAdded = true;
        }

        // 匹配活跃度 当前时间-活跃度=一个临界时间点，如果刷新时间在临界时间点之后（大于等于），说明符合条件
        if (activeTimes != null && activeTimes > 0) {

            LocalDateTime tempTime  = LocalDateUtils.minus(LocalDateTime.now(),
                                                            activeTimes,
                                                            ChronoUnit.SECONDS);
            String timePoint = LocalDateUtils.format(tempTime,
                                                     LocalDateUtils.DATETIME_PATTERN);

            boolQueryBuilder.must(QueryBuilders.rangeQuery("refreshTime")
                    .gte(timePoint)
            );

            conditionAdded = true;
        }

        // 匹配工作年限
        if (beginWorkExpYears >= 0 && endWorkExpYears > 0) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("workYears")
                    .gte(beginWorkExpYears)
                    .lte(endWorkExpYears)
            );
            conditionAdded = true;
        }

        // 匹配学历
        if (StringUtils.isNotBlank(edu)) {
            // 只查edu字段，只查一个，不多查
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("education", edu));
            conditionAdded = true;
        }

        // 匹配薪资
        if (beginSalary >= 0 && endSalary > 0) {

            BoolQueryBuilder bool1 =  QueryBuilders.boolQuery()
                    .must(QueryBuilders.rangeQuery("beginSalary").lte(beginSalary))
                    .must(QueryBuilders.rangeQuery("endSalary").gte(beginSalary));

            BoolQueryBuilder bool2 =  QueryBuilders.boolQuery()
                    .must(QueryBuilders.rangeQuery("beginSalary").lte(endSalary))
                    .must(QueryBuilders.rangeQuery("endSalary").gte(endSalary));

            boolQueryBuilder.must(QueryBuilders.boolQuery()
                    .should(bool1)
                    .should(bool2)
            );

            conditionAdded = true;
        }

        // 匹配求职状态
        if (StringUtils.isNotBlank(jobStatus)) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("jobStatus", jobStatus));
            conditionAdded = true;
        }


        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        // 条件判断是否需要添加搜索的条件
        if (conditionAdded) {
            builder.withQuery(boolQueryBuilder);
        } else {
            builder.withQuery(QueryBuilders.matchAllQuery());
        }

        Query query = builder.withPageable(pageable).build();

        SearchHits<SearchResumesEO> searchHits = esTemplate.search(query, SearchResumesEO.class);
        List<SearchResumesEO> list = getList(searchHits, SearchResumesEO.class);

        System.out.println("==============================");
        System.out.println(list);
        System.out.println("==============================");

        // 状态grid
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page + 1);

        return gridResult;
    }

    private UsersVO getUserInfoVO(String userId) {
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.get(userId);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO hrUser = JsonUtils.jsonToPojo(json, UsersVO.class);
        return hrUser;
    }

    private <T> List<T> getList(SearchHits<T> searchHits, Class<T> clazz) {
        List<SearchHit<T>> hits = searchHits.getSearchHits();
        List<T> list = new ArrayList<>();
        for (SearchHit<T> searchHit : hits) {
            T res = searchHit.getContent();
            list.add(res);
        }
        return list;
    }

    @Override
    public List<SearchResumesEO> searchCollectResumes(List<String> ids) {

        List<SearchResumesEO> resumesEOList = new ArrayList<>();
        if (ids.isEmpty()) {
            return resumesEOList;
        }

        IdsQueryBuilder queryBuilder = new IdsQueryBuilder();
        for (String id : ids) {
            queryBuilder.addIds(id);
        }

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.should(queryBuilder);

        Query query = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .build();

        SearchHits<SearchResumesEO> searchHits = esTemplate.search(query, SearchResumesEO.class);

        return getList(searchHits, SearchResumesEO.class);
    }
}
