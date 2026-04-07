package com.liepin.controller;

import com.liepin.base.BaseInfoProperties;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.eo.SearchResumesEO;
import com.liepin.pojo.mo.*;
import com.liepin.pojo.vo.SearchJobsVO;
import com.liepin.service.CountsService;
import com.liepin.service.JobService;
import com.liepin.service.ResumeSearchService;
import com.liepin.utils.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("counts")
public class CountsController extends BaseInfoProperties {

    @Autowired
    private CountsService countsService;

    @Autowired
    private ResumeSearchService resumeSearchService;

    @Autowired
    private JobService jobService;

    /****************************** HR收藏简历 start ******************************/

    @PostMapping("addCollectResume")
    public GraceJSONResult addCollectResume(String hrId, String resumeExpectId) {
        countsService.addCollect(hrId, resumeExpectId);
        return GraceJSONResult.ok();
    }

    @PostMapping("removeCollectResume")
    public GraceJSONResult removeCollectResume(String hrId, String resumeExpectId) {
        countsService.removeCollect(hrId, resumeExpectId);
        return GraceJSONResult.ok();
    }

    @PostMapping("isHrCollectResume")
    public GraceJSONResult isHrCollectResume(String hrId, String resumeExpectId) {
        return GraceJSONResult.ok(countsService.isHrCollectResume(hrId, resumeExpectId));
    }

    @PostMapping("getColletResumeCounts")
    public GraceJSONResult getColletResumeCounts(String hrId) {

        //Integer collectResumeCounts = 0;
        //
        //String countsStr = redis.get(HR_COLLECT_RESUME_COUNTS + ":" + hrId);
        //if (StringUtils.isBlank(countsStr)) {
        //    return GraceJSONResult.ok(collectResumeCounts);
        //}
        //
        //collectResumeCounts = Integer.valueOf(countsStr);

        return GraceJSONResult.ok(getCountsConvent(HR_COLLECT_RESUME_COUNTS + ":" + hrId));
    }

    @PostMapping("pagedCollectResumeList")
    public GraceJSONResult pagedCollectResumeList(String hrId,
                                                  Integer page,
                                                  Integer pageSize) {

        if (page == null) page = 0;
        if (pageSize == null) page = 10;

        // 1. 查询HR收藏简历的关系
        List<HrCollectResumeMO> list = countsService.queryPagedCollectResumeList(hrId,
                                                                                page,
                                                                                pageSize);
        List<String> idsList = list.stream()
                                    .map(HrCollectResumeMO::getResumeExpectId)
                                    .collect(Collectors.toList());

        // 2. 根据获得的简历期望id列表，去es中查询列表
        List<SearchResumesEO> resumesEOList = resumeSearchService.searchCollectResumes(idsList);

        // 3. 处理收藏的时间并且显示
        for (SearchResumesEO eo : resumesEOList) {
            for (HrCollectResumeMO mo : list) {
                if (mo.getResumeExpectId().equalsIgnoreCase(eo.getResumeExpectId())) {
                    eo.setHrCollectResumeTime(LocalDateUtils.format(
                                                        mo.getCreateTime(),
                                                        LocalDateUtils.DATETIME_PATTERN));
                }
            }
        }

        return GraceJSONResult.ok(resumesEOList);
    }

    /****************************** HR收藏简历 end ******************************/

    /****************************** HR浏览简历历史记录 end ******************************/

    @PostMapping("addReadResumeRecord")
    public GraceJSONResult addReadResumeRecord(String hrId, String resumeExpectId) {
        countsService.saveReadResumeRecord(hrId, resumeExpectId);
        return GraceJSONResult.ok();
    }

    @PostMapping("getReadResumeRecordCounts")
    public GraceJSONResult getReadResumeRecordCounts(String hrId) {
        return GraceJSONResult.ok(getCountsConvent(HR_READ_RESUME_RECORD_COUNTS + ":" + hrId));
    }

    @PostMapping("pagedReadResumeRecordList")
    public GraceJSONResult pagedReadResumeRecordList(String hrId,
                                                  Integer page,
                                                  Integer pageSize) {

        if (page == null) page = COMMON_START_PAGE_ZERO;
        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        // 1. 查询HR浏览阅读简历的历史
        List<HrReadResumeRecordMO> list = countsService.queryPagedReadResumeList(hrId,
                                                                            page,
                                                                            pageSize);
        List<String> idsList = list.stream()
                .map(HrReadResumeRecordMO::getResumeExpectId)
                .collect(Collectors.toList());

        // 2. 根据获得的简历期望id列表，去es中查询列表
        List<SearchResumesEO> resumesEOList = resumeSearchService.searchCollectResumes(idsList);

        // 3. 处理收藏的时间并且显示
        for (SearchResumesEO eo : resumesEOList) {
            for (HrReadResumeRecordMO mo : list) {
                if (mo.getResumeExpectId().equalsIgnoreCase(eo.getResumeExpectId())) {
                    eo.setHrReadResumeTime(LocalDateUtils.format(
                            mo.getCreateTime(),
                            LocalDateUtils.DATETIME_PATTERN));
                }
            }
        }

        List finalList = resumesEOList.stream()
                .sorted(Comparator.comparing(
                        SearchResumesEO::getHrReadResumeTime).reversed())
                .collect(Collectors.toList());

        return GraceJSONResult.ok(finalList);
    }

    /****************************** HR浏览简历历史记录 end ******************************/

    /****************************** 谁看过我 - HR查看用户简历 start ******************************/

    @PostMapping("hrLookCand")
    public GraceJSONResult hrLookCand(@RequestBody @Valid WhoLookMeMO whoLookMeMO) {
        countsService.saveWhoLookMe(whoLookMeMO);
        return GraceJSONResult.ok();
    }

    @PostMapping("getWhoLookMeCounts")
    public GraceJSONResult getWhoLookMeCounts(String candUserId) {
        return GraceJSONResult.ok(getCountsConvent(WHO_LOOK_ME_COUNTS + ":" + candUserId));
    }

    @PostMapping("pagedWhoLookMe")
    public GraceJSONResult pagedWhoLookMe(String candUserId,
                                                     Integer page,
                                                     Integer pageSize) {

        if (page == null) page = COMMON_START_PAGE_ZERO;
        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        return GraceJSONResult.ok(countsService.pagedWhoLookMe(candUserId, page, pageSize));
    }

    /****************************** 谁看过我 - HR查看用户简历 end ******************************/

    /****************************** 求职者关注HR start ******************************/


    @PostMapping("followHr")
    public GraceJSONResult followHr(@RequestBody @Valid CandFollowHrMO followHrMO) {
        countsService.saveFollowHr(followHrMO);
        return GraceJSONResult.ok();
    }

    @PostMapping("unfollowHr")
    public GraceJSONResult unfollowHr(String hrId, String candUserId) {
        countsService.deleteFollowHr(candUserId, hrId);
        return GraceJSONResult.ok();
    }

    @PostMapping("doseCandFollowHr")
    public GraceJSONResult doseCandFollowHr(String candUserId, String hrId) {
        return GraceJSONResult.ok(countsService.doesCandFollowHr(candUserId, hrId));
    }

    @PostMapping("getCandFollowHrCounts")
    public GraceJSONResult getCandFollowHrCounts(String candUserId) {
        return GraceJSONResult.ok(getCountsConvent(CAND_FOLLOW_HR_COUNTS + ":" + candUserId));
    }

    @PostMapping("pagedCandFollowHr")
    public GraceJSONResult pagedCandFollowHr(String candUserId,
                                          Integer page,
                                          Integer pageSize) {

        if (page == null) page = COMMON_START_PAGE_ZERO;
        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        return GraceJSONResult.ok(countsService.pagedCandFollowHr(candUserId, page, pageSize));
    }

    /****************************** 求职者关注HR end ******************************/

    /****************************** 求职者收藏职位 start ******************************/

    @PostMapping("addCollectJob")
    public GraceJSONResult addCollectJob(String candUserId, String jobId) {
        countsService.addCollectJob(candUserId, jobId);
        return GraceJSONResult.ok();
    }

    @PostMapping("removeCollectJob")
    public GraceJSONResult removeCollectJob(String candUserId, String jobId) {
        countsService.removeCollectJob(candUserId, jobId);
        return GraceJSONResult.ok();
    }

    @PostMapping("isCandCollectJob")
    public GraceJSONResult isCandCollectJob(String candUserId, String jobId) {
        return GraceJSONResult.ok(countsService.isCandCollectJob(candUserId, jobId));
    }

    @PostMapping("getCollectJobCounts")
    public GraceJSONResult getCollectJobCounts(String candUserId) {
        return GraceJSONResult.ok(getCountsConvent(CAND_COLLECT_JOB_COUNTS + ":" + candUserId));
    }

    @PostMapping("pagedCollectJobList")
    public GraceJSONResult pagedCollectJobList(String candUserId,
                                               Integer page,
                                               Integer pageSize) {

        if (page == null) page = COMMON_START_PAGE_ZERO;
        if (pageSize == null) pageSize = COMMON_PAGE_SIZE;

        List<CandCollectJobMO> jobMOList = countsService.pagedCollectJobList(candUserId, page, pageSize);

        List<String> jobIdList = jobMOList.stream()
                                            .map(CandCollectJobMO::getJobId)
                                            .collect(Collectors.toList());

        // 根据jobIdList再去查询收藏的职位列表
        List<SearchJobsVO> jobsVOList = jobService.searchCollectJobs(jobIdList);

        for (SearchJobsVO vo : jobsVOList) {
            for (CandCollectJobMO mo : jobMOList) {
                if (mo.getJobId().equalsIgnoreCase(vo.getId())) {
                    vo.setCollectTime(mo.getCreateTime());
                }
            }
        }

        List finalList = jobsVOList.stream()
                            .sorted(Comparator.comparing(
                                    SearchJobsVO::getCollectTime)
                                    .reversed())
                            .collect(Collectors.toList());

        return GraceJSONResult.ok(finalList);
    }

    /****************************** 求职者收藏职位 end ******************************/

}
