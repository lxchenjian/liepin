package com.liepin.controller;

import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.eo.SearchResumesEO;
import com.liepin.pojo.eo.Stu;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("es")
public class HelloController {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @GetMapping("hello")
    public Object hello() {
        return "Hello Elasticsearch~~~";
    }

    @GetMapping("createIndex")
    public Object createIndex() {
        esTemplate.indexOps(Stu.class).create();
        return GraceJSONResult.ok();
    }

    @GetMapping("deleteIndex")
    public Object deleteIndex() {
        esTemplate.indexOps(Stu.class).delete();
        return GraceJSONResult.ok();
    }

    /**
     * 这种创建方式不太好，字段类型无法把控
     * @return
     */
    @GetMapping("save")
    public Object save() {

        Stu stu = new Stu();
        stu.setStuId(1002L);
        stu.setName("imooc-慕课网-A");
        stu.setAge(22);
        stu.setMoney(500.8f);
        stu.setDescription("学习架构师2.0课程~~");

        IndexQuery iq = new IndexQueryBuilder().withObject(stu).build();

        IndexCoordinates ic = IndexCoordinates.of("stu");

        esTemplate.index(iq, ic);
        return GraceJSONResult.ok();
    }

    @GetMapping("update")
    public Object update() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "慕课网a");
        map.put("age", 22);
        map.put("money", 600.6f);
        map.put("description", "学习架构师2.0课程~~");

        UpdateQuery uq = UpdateQuery.builder("1001")
                                    .withDocument(Document.from(map))
                                    .build();

        esTemplate.update(uq, IndexCoordinates.of("stu"));

        return GraceJSONResult.ok();
    }

    @GetMapping("get")
    public Object get() {

        Criteria criteria = new Criteria().and("stuId").is(1001);
        Query query = new CriteriaQuery(criteria);
        Stu stu = esTemplate.searchOne(query, Stu.class).getContent();


        Criteria criteria2 = new Criteria("age").is(22).and("money").greaterThan(500);
        Query query2 = new CriteriaQuery(criteria2);
        SearchHits<Stu> searchHits = esTemplate.search(query2, Stu.class);
        List<SearchHit<Stu>> hits = searchHits.getSearchHits();
        List<Stu> stuList = new ArrayList<>();
        for (SearchHit<Stu> searchHit : hits) {
            Stu res = searchHit.getContent();
            stuList.add(res);
        }

        return GraceJSONResult.ok(stuList);
    }

    @GetMapping("delete")
    public Object delete() {
        esTemplate.delete("1002", IndexCoordinates.of("stu"));
        return GraceJSONResult.ok();
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("match_all")
    public Object matchAll(Integer page, Integer pageSize) {

        // es的页面是从0开始的，所以此处page需要-1
        if (page < 1) page = 1;
        page--;
        Pageable pageable = PageRequest.of(page, pageSize);

        // NativeSearchQueryBuilder 多条件查询构造器（复杂查询）
        Query query = new NativeSearchQueryBuilder()
                            .withQuery(QueryBuilders.matchAllQuery())
                            .withPageable(pageable)
                            .build();

        SearchHits<SearchResumesEO> searchHits = esTemplate.search(query, SearchResumesEO.class);
        List<SearchResumesEO> list = getList(searchHits, SearchResumesEO.class);

        return GraceJSONResult.ok(list);
    }

    @GetMapping("term")
    public Object term() {

        // NativeSearchQueryBuilder 多条件查询构造器（复杂查询）
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("sex", 1))
                //.withQuery(QueryBuilders.termQuery("beginSalary", 45))
                .withQuery(QueryBuilders.matchQuery("major", "会计理财"))
                .build();

        SearchHits<SearchResumesEO> searchHits = esTemplate.search(query, SearchResumesEO.class);
        List<SearchResumesEO> list = getList(searchHits, SearchResumesEO.class);

        return GraceJSONResult.ok(list);
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
}