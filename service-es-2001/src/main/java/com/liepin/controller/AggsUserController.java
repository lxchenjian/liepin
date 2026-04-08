package com.liepin.controller;

import com.liepin.enums.AggsUsersType;
import com.liepin.enums.Sex;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.ao.MapUserCountsAO;
import com.liepin.pojo.ao.SendResumeDailyAO;
import com.liepin.pojo.ao.UserCountsAO;
import com.liepin.pojo.eo.AggsUsersEO;
import com.liepin.utils.LocalDateUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("aggs")
public class AggsUserController {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @GetMapping("save")
    public GraceJSONResult save() {

        AggsUsersEO usersEO1 = new AggsUsersEO(AggsUsersType.USER.type,
                                            "user-3003",
                                            "",
                                            "",
                                            "风间影月",
                                            Sex.man.type,
                                            "face",
                                            "北京",
                                            "动漫动画",
                                            "",
                                            "2023-04-02");
        AggsUsersEO usersEO2 = new AggsUsersEO(AggsUsersType.HR.type,
                                        "user-10013",
                                        "",
                                        "",
                                        "风间影月",
                                        1,
                                        "face",
                                        "北京",
                                        "电子游戏" ,
                                        "" ,
                                        "2021-03-28");
        AggsUsersEO usersEO3 = new AggsUsersEO(AggsUsersType.COMPANY.type,
                                        "",
                                        "company-300001",
                                        "",
                                        "",
                                        2,
                                        "",
                                        "上海",
                                        "电子游戏" ,
                                        "慕课网" ,
                                        "2020-03-28");
        AggsUsersEO usersEO4 = new AggsUsersEO(AggsUsersType.ENTRY.type,
                                        "user-10014",
                                        "company-300001",
                                        "resume-400001",
                                        "风间影月",
                                        2,
                                        "face",
                                        "北京",
                                        "医疗医药" ,
                                        "慕课网" ,
                                        "2022-03-28");
        AggsUsersEO usersEO5 = new AggsUsersEO(AggsUsersType.SEND.type,
                                        "user-10015",
                                        "",
                                        "resume-400001",
                                        "风间影月",
                                        1,
                                        "face",
                                        "上海",
                                        "医疗医药" ,
                                        "" ,
                                        "2022-03-28");


        esTemplate.save(usersEO1, usersEO2, usersEO3, usersEO4, usersEO5);

        return GraceJSONResult.ok();
    }


    /**
     * 统计不同类型用户总数
     * {
     *     "size": 0, // 分页数据就没有了
     *     "aggs": {
     *         "abc_counts": {
     *             "terms": {
     *                 "field": "type"
     *             }
     *         }
     *     }
     * }
     */
    @GetMapping("userCountsTotal")
    public GraceJSONResult userCountsTotal() {

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("abc_counts")
                .field("type");

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withAggregations(aggregationBuilder)
                .build();

        SearchHits<AggsUsersEO> searchHits = esTemplate.search(searchQuery, AggsUsersEO.class);

        Aggregations aggregations = (Aggregations)searchHits
                                                    .getAggregations()
                                                    .aggregations();
        Terms terms = aggregations.get("abc_counts");

        List<? extends Terms.Bucket> termsBuckets = terms.getBuckets();
        Map<String, Long> countsMap = new HashMap<>();
        termsBuckets.stream().forEach(res -> {
            String type = res.getKeyAsString();
            Long counts = res.getDocCount();

            countsMap.put(type, counts);
        });

        return GraceJSONResult.ok(countsMap);
    }

    /**
     * 统计日期区间折线图
     * {
     *     "query": {
     *         "bool": {
     *             "must": [
     *                 {
     *                     "term": {
     *                         "type": "2"
     *                     }
     *                 },
     *                 {
     *                     "range": {
     *                         "createDate": {
     *                             "gte": "2023-03-25",
     *                             "lte": "2023-04-03"
     *                         }
     *                     }
     *                 }
     *             ]
     *         }
     *     },
     *     "aggs": {
     *         "date_counts": {
     *             "date_histogram": { // 关键字
     *                 "field": "createDate",
     *                 "min_doc_count": 0,// 没有数据，显示0
     *                 "interval": "1d",// 日期间隔
     *                 "format": "yyyy-MM-dd",// 时间格式
     *                 // "offset": "-8h" // 时区，有8小时时差
     *                 "extended_bounds" : {
     *                     "min": "2023-03-25",
     *                     "max": "2023-04-03"
     *                 }
     *             }
     *         }
     *     }
     * }
     */
    // 减少查询次数
    @GetMapping("userCountsDaily")
    public GraceJSONResult userCountsDaily() {

        List<Long> aoList1 = getDailyByUserType(AggsUsersType.USER);
        List<Long> aoList2 = getDailyByUserType(AggsUsersType.HR);
        List<Long> aoList3 = getDailyByUserType(AggsUsersType.COMPANY);
        List<Long> aoList4 = getDailyByUserType(AggsUsersType.ENTRY);

        Map<String, Object> lineCountsMap = new HashMap<>();
        lineCountsMap.put("newUsers", aoList1);
        lineCountsMap.put("newHRs", aoList2);
        lineCountsMap.put("newCompanies", aoList3);
        lineCountsMap.put("successCandidates", aoList4);

        List<String> daysList = get7Days();
        lineCountsMap.put("days", daysList);

        return GraceJSONResult.ok(lineCountsMap);
    }

    private List<String> get7Days() {
        List<String> list = new ArrayList<>();
        for (int i = 7 ;  i > 0 ; i --) {
            String dayBefore = LocalDateUtils.format(
                    LocalDateUtils.minus(LocalDateTime.now(), i, ChronoUnit.DAYS),
                    LocalDateUtils.DATE_DAY_PATTERN_SHORT);
            list.add(dayBefore);
        }
        return list;
    }

    private List<Long> getDailyByUserType(AggsUsersType usersType) {

        // 计算今天和7天前的日期
        String today = LocalDateUtils.getTodayStr();
        String dayBeforeAWeek = LocalDateUtils.format(
                LocalDateUtils.minus(LocalDateTime.now(), 7, ChronoUnit.DAYS),
                LocalDateUtils.DATE_PATTERN);

        // 聚合构造器
        DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders
                .dateHistogram("date_counts")
                .field("createDate")
                .minDocCount(0)
                .calendarInterval(DateHistogramInterval.DAY)
                .format(LocalDateUtils.DATE_PATTERN)
                //.offset("-8h")
                .extendedBounds(new LongBounds(dayBeforeAWeek, today));

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("type", usersType.type));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("createDate")
                                                            .gte(dayBeforeAWeek)
                                                            .lte(today)
        );

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withAggregations(aggregationBuilder)
                .build();
        SearchHits<AggsUsersEO> searchHits = esTemplate.search(searchQuery, AggsUsersEO.class);
        Aggregations aggregations = (Aggregations)searchHits
                .getAggregations()
                .aggregations();

        ParsedDateHistogram dateHistogram = aggregations.get("date_counts");
        List<? extends Histogram.Bucket> dateHistogramBuckets = dateHistogram.getBuckets();

        List<Long> lineList = new ArrayList<>();
        dateHistogramBuckets.stream().forEach(res -> {
            Long counts = res.getDocCount();
            lineList.add(counts);
        });

        return lineList;
    }

    /**
     * 地图区域可视化统计
     * {
     *     "size": 0,
     *     "query": {
     *         "term": {
     *             "industry": "房地产"
     *         }
     *     },
     *     "aggs": {
     *         "abc_counts": {
     *             "terms": {
     *                 "field": "province"
     *             }
     *         }
     *     }
     * }
     */
    @GetMapping("userCountsMap")
    public GraceJSONResult userCountsMap() {

        String industry = "电子游戏";
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("industry", industry);

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("map_counts")
                .field("province");

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withAggregations(aggregationBuilder)
                .build();
        SearchHits<AggsUsersEO> searchHits = esTemplate.search(searchQuery, AggsUsersEO.class);
        Aggregations aggregations = (Aggregations)searchHits
                .getAggregations()
                .aggregations();

        Terms terms = aggregations.get("map_counts");

        List<? extends Terms.Bucket> termsBuckets = terms.getBuckets();

        List<MapUserCountsAO> aoList = new ArrayList<>();
        termsBuckets.stream().forEach(res -> {
            String province = res.getKeyAsString();
            Long counts = res.getDocCount();

            MapUserCountsAO countsAO = new MapUserCountsAO();
            countsAO.setName(province);
            countsAO.setValue(counts);

            aoList.add(countsAO);
        });

        return GraceJSONResult.ok(aoList);
    }

    /**
     * 雷达图统计多行业从业人数
     * {
     *     "size": 0,
     *     "query": {
     *         "bool": {
     *             "must": [
     *                 {
     *                     "term": {
     *                         "industry": "互联网"
     *                     }
     *                 },
     *                 {
     *                     "range": {
     *                         "createDate": {
     *                             "gte": "2014-01-01",
     *                             "lte": "2023-12-31"
     *                         }
     *                     }
     *                 }
     *             ]
     *         }
     *     },
     *     "aggs": {
     *         "year_counts": {
     *             "date_histogram": {
     *                 "field": "createDate",
     *                 "min_doc_count": 0,
     *                 "interval": "1y",
     *                 "format": "yyyy",
     *                 "extended_bounds" : {
     *                     "min": "2014",
     *                     "max": "2023"
     *                 }
     *             }
     *         }
     *     }
     * }
     */
    @GetMapping("userCountsRaddar")
    public GraceJSONResult userCountsRaddar() {

        String industry1 = "互联网";
        String industry2 = "医疗医药";
        String industry3 = "金融保险";
        String industry4 = "电子游戏";
        String industry5 = "动画动漫";
        String industry6 = "房地产";

        List<UserCountsAO> aoList1 = getRaddarCounts(industry1);
        List<UserCountsAO> aoList2 = getRaddarCounts(industry2);
        List<UserCountsAO> aoList3 = getRaddarCounts(industry3);
        List<UserCountsAO> aoList4 = getRaddarCounts(industry4);
        List<UserCountsAO> aoList5 = getRaddarCounts(industry5);
        List<UserCountsAO> aoList6 = getRaddarCounts(industry6);

        List<List<UserCountsAO>> result = new ArrayList<>();
        result.add(aoList1);
        result.add(aoList2);
        result.add(aoList3);
        result.add(aoList4);
        result.add(aoList5);
        result.add(aoList6);

        return GraceJSONResult.ok(result);
    }

    private List<UserCountsAO> getRaddarCounts(String industry) {

        // 计算今年和10年前的年份 yyyy
        String thisYear = LocalDateUtils.getTodayStr(LocalDateUtils.YEAR_DATE_PATTERN);
        String before10Year = LocalDateUtils.format(
                LocalDateUtils.minus(LocalDateTime.now(), 10-1, ChronoUnit.YEARS),
                LocalDateUtils.YEAR_DATE_PATTERN);

        // 计算今年和10年前的日期 yyyy-MM-dd
        // 今年 + “-12-30”
        String thisYearDateEnd = thisYear + "-12-30";
        // 10年前 + “-01-01”
        String before10YearDateStart = before10Year + "-01-01";

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("industry", industry));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("createDate")
                .gte(before10YearDateStart)
                .lte(thisYearDateEnd)
        );

        // 聚合构造器
        DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders
                .dateHistogram("year_counts")
                .field("createDate")
                .minDocCount(0)
                .calendarInterval(DateHistogramInterval.YEAR)
                .format(LocalDateUtils.YEAR_DATE_PATTERN)
                .extendedBounds(new LongBounds(before10Year, thisYear));

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withAggregations(aggregationBuilder)
                .build();
        SearchHits<AggsUsersEO> searchHits = esTemplate.search(searchQuery, AggsUsersEO.class);
        Aggregations aggregations = (Aggregations)searchHits
                .getAggregations()
                .aggregations();

        ParsedDateHistogram dateHistogram = aggregations.get("year_counts");
        List<? extends Histogram.Bucket> dateHistogramBuckets = dateHistogram.getBuckets();

        List<UserCountsAO> aoList = new ArrayList<>();
        dateHistogramBuckets.stream().forEach(res -> {
            Long counts = res.getDocCount();
            String year = res.getKeyAsString();

            UserCountsAO countsAO = new UserCountsAO();
            countsAO.setYear(year);
            countsAO.setCounts(counts);
            countsAO.setIndustry(industry);

            aoList.add(countsAO);
        });

        return aoList;
    }

    /**
     * 饼状图行业男女比例
     * {
     *     "size": 0,
     *     "query": {
     *         "bool": {
     *             "must": [
     *                 {
     *                     "term": {
     *                         "industry": "房地产"
     *                     }
     *                 }
     *             ],
     *             "should": [
     *                 {
     *                     "term": {
     *                         "sex": "1"
     *                     }
     *                 },
     *                 {
     *                     "term": {
     *                         "sex": "2"
     *                     }
     *                 }
     *             ]
     *         }
     *     },
     *     "aggs": {
     *         "sex_counts": {
     *             "terms": {
     *                 "field": "sex"
     *             }
     *         }
     *     }
     * }
     */
    @GetMapping("userCountsSex")
    public GraceJSONResult userCountsSex() {

        String industry = "互联网";

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("industry", industry));
        boolQueryBuilder.should(QueryBuilders.boolQuery()
                                .should(QueryBuilders.termQuery("sex", Sex.man.type))
                                .should(QueryBuilders.termQuery("sex", Sex.woman.type))
        );

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("sex_counts")
                .field("sex");

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withAggregations(aggregationBuilder)
                .build();
        SearchHits<AggsUsersEO> searchHits = esTemplate.search(searchQuery, AggsUsersEO.class);
        Aggregations aggregations = (Aggregations)searchHits
                .getAggregations()
                .aggregations();

        Terms terms = aggregations.get("sex_counts");

        List<? extends Terms.Bucket> termsBuckets = terms.getBuckets();

        Map<String, Object> sexCountsMap = new HashMap<>();
        termsBuckets.stream().forEach(res -> {
            String sex = res.getKeyAsString();
            Long counts = res.getDocCount();

            UserCountsAO countsAO = new UserCountsAO();
            //countsAO.setSex(sex);
            countsAO.setCounts(counts);
            countsAO.setIndustry(industry);

            sexCountsMap.put(sex, countsAO);
        });

        return GraceJSONResult.ok(sexCountsMap);
    }

    /**
     * 柱状图每日投递简历数与男女比例
     * {
     *     "size": 0,
     *     "query": {
     *         "bool": {
     *             "must": [
     *                 {
     *                     "term": {
     *                         "type": "5"
     *                     }
     *                 },
     *                 {
     *                     "term": {
     *                         "sex": "0"
     *                     }
     *                 },
     *                 {
     *                     "range": {
     *                         "createDate": {
     *                             "gte": "2023-03-25",
     *                             "lte": "2023-04-03"
     *                         }
     *                     }
     *                 }
     *             ]
     *         }
     *     },
     *     "aggs": {
     *         "date_counts": {
     *             "date_histogram": {
     *                 "field": "createDate",
     *                 "min_doc_count": 0,
     *                 "interval": "1d",
     *                 "format": "yyyy-MM-dd",
     *                 "extended_bounds": {
     *                     "min": "2023-03-25",
     *                     "max": "2023-04-03"
     *                 }
     *             }
     *         }
     *     }
     * }
     */
    @GetMapping("userCountsDailyResumeCounts")
    public GraceJSONResult userCountsDailyResumeCounts() {

        List<Long> manList = getDailyBySex(Sex.man);
        List<Long> womanList = getDailyBySex(Sex.woman);
        SendResumeDailyAO resumeDailyAO = new SendResumeDailyAO(manList, womanList);

        Map<String, Object> countsMap = new HashMap<>();
        countsMap.put("resumeDailyAO", resumeDailyAO);
        countsMap.put("days", get7Days());

        return GraceJSONResult.ok(countsMap);
    }

    private List<Long> getDailyBySex(Sex sex) {

        // 计算今天和7天前的日期
        String today = LocalDateUtils.getTodayStr();
        String dayBeforeAWeek = LocalDateUtils.format(
                LocalDateUtils.minus(LocalDateTime.now(), 7, ChronoUnit.DAYS),
                LocalDateUtils.DATE_PATTERN);

        // 聚合构造器
        DateHistogramAggregationBuilder aggregationBuilder = AggregationBuilders
                .dateHistogram("resume_daily_counts")
                .field("createDate")
                .minDocCount(0)
                .calendarInterval(DateHistogramInterval.DAY)
                .format(LocalDateUtils.DATE_PATTERN)
                .extendedBounds(new LongBounds(dayBeforeAWeek, today));

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //boolQueryBuilder.must(QueryBuilders.termQuery("type", AggsUsersType.SEND.type));
        boolQueryBuilder.must(QueryBuilders.termQuery("sex", sex.type));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("createDate")
                .gte(dayBeforeAWeek)
                .lte(today)
        );

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withAggregations(aggregationBuilder)
                .build();
        SearchHits<AggsUsersEO> searchHits = esTemplate.search(searchQuery, AggsUsersEO.class);
        Aggregations aggregations = (Aggregations)searchHits
                .getAggregations()
                .aggregations();

        ParsedDateHistogram dateHistogram = aggregations.get("resume_daily_counts");
        List<? extends Histogram.Bucket> dateHistogramBuckets = dateHistogram.getBuckets();

        List<Long> countsList = new ArrayList<>();
        dateHistogramBuckets.stream().forEach(res -> {
            Long counts = res.getDocCount();
            countsList.add(counts);
        });

        return countsList;
    }
}