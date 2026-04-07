package com.liepin.controller;

import com.liepin.base.BaseInfoProperties;
import com.liepin.enums.ArticleStatus;
import com.liepin.grace.result.GraceJSONResult;
import com.liepin.pojo.Article;
import com.liepin.pojo.bo.NewArticleBO;
import com.liepin.pojo.vo.ArticleVO;
import com.liepin.service.ArticleService;
import com.liepin.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("article")
public class ArticleController extends BaseInfoProperties {

    @Autowired
    private ArticleService articleService;

    @PostMapping("save")
    public GraceJSONResult saveOrUpdate(@RequestBody @Valid NewArticleBO articleBO) {

        // TODO NewArticleBO 字段自行校验

        String articleId = articleBO.getArticleId();

        if (StringUtils.isBlank(articleId)) {
            articleService.createArticle(articleBO);
        } else {
            articleService.updateArticle(articleBO);
        }

        return GraceJSONResult.ok();
    }

    @PostMapping("list")
    public GraceJSONResult list(Integer page, Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        PagedGridResult gridResult = articleService.queryArticleList(null,
                                                                    page,
                                                                    limit);
        gridResult = dealReadCounts(gridResult);

        return GraceJSONResult.ok(gridResult);
    }

    @PostMapping("detail")
    public GraceJSONResult detail(String articleId) {
        return GraceJSONResult.ok(articleService.queryArticleDetail(articleId, null));
    }

    @PostMapping("delete")
    public GraceJSONResult delete(String articleId) {
        articleService.updateStatus(articleId, ArticleStatus.DELETE);
        return GraceJSONResult.ok();
    }

    @PostMapping("open")
    public GraceJSONResult open(String articleId) {
        articleService.updateStatus(articleId, ArticleStatus.OPEN);
        return GraceJSONResult.ok();
    }

    @PostMapping("close")
    public GraceJSONResult close(String articleId) {
        articleService.updateStatus(articleId, ArticleStatus.CLOSE);
        return GraceJSONResult.ok();
    }


    // ============================== 提供给用户端使用 ==============================


    @PostMapping("app/list")
    public GraceJSONResult appList(Integer page, Integer limit) {

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        PagedGridResult gridResult = articleService.queryArticleList(ArticleStatus.OPEN,
                                                                    page,
                                                                    limit);
        gridResult = dealReadCounts(gridResult);

        return GraceJSONResult.ok(gridResult);
    }

    @PostMapping("app/detail")
    public GraceJSONResult appDetail(String articleId) {
        return GraceJSONResult.ok(articleService.queryArticleDetail(articleId, ArticleStatus.OPEN));
    }

    @PostMapping("app/read")
    public GraceJSONResult read(String userId, String articleId) {

        String isRead = redis.get(REDIS_USER_READ_ARTICLE + ":" + userId + ":" + articleId);

        if (StringUtils.isBlank(isRead)) {
            // 阅读总数累加1 线程安全的
            redis.increment(REDIS_ARTICLE_READ_COUNTS + ":" + articleId, 1);

            // 标记用户阅读
            redis.set(REDIS_USER_READ_ARTICLE + ":" + userId + ":" + articleId, articleId);
        }

        return GraceJSONResult.ok();
    }


    private PagedGridResult dealReadCounts(PagedGridResult gridResult) {

        List<Article> list = (List<Article>)gridResult.getRows();

        List<String> articleIdList = list.stream().map(article -> {
            return REDIS_ARTICLE_READ_COUNTS + ":" + article.getId();
        }).collect(Collectors.toList());

        List<String> countsList = redis.mget(articleIdList);

        List<ArticleVO> articleVOList = new ArrayList<>();
        for (int i = 0 ; i < list.size() ; i ++) {
            ArticleVO articleVO = new ArticleVO();

            Article temp = list.get(i);
            BeanUtils.copyProperties(temp, articleVO);

            String countsStr = countsList.get(i);
            long counts = StringUtils.isNotBlank(countsStr) ? Long.valueOf(countsStr) : 0;
            articleVO.setReadCounts(counts);

            articleVOList.add(articleVO);
        }

        gridResult.setRows(articleVOList);
        return gridResult;
    }
}
