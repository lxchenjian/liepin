package com.liepin.service;

import com.liepin.enums.ArticleStatus;
import com.liepin.pojo.Article;
import com.liepin.pojo.bo.NewArticleBO;
import com.liepin.utils.PagedGridResult;

/**
 * <p>
 * 文章表 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
public interface ArticleService {

    /**
     * 发布文章
     */
    public void createArticle(NewArticleBO articleBO);

    /**
     * 修改文章
     */
    public void updateArticle(NewArticleBO articleBO);


    /**
     * 修改文章状态
     * @param articleId
     * @param status
     */
    public void updateStatus(String articleId, ArticleStatus status);

    /**
     * 查询文章列表
     * @param status
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryArticleList(ArticleStatus status,
                                            Integer page,
                                            Integer pageSize);

    /**
     * 查询文章详情
     * @param articleId
     * @return
     */
    public Article queryArticleDetail(String articleId, ArticleStatus status);

}
