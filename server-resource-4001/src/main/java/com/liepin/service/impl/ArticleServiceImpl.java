package com.liepin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.liepin.intercept.JWTCurrentUserInterceptor;
import com.liepin.mq.DelayConfig_Article;
import com.liepin.base.BaseInfoProperties;
import com.liepin.enums.ArticleStatus;
import com.liepin.mapper.ArticleMapper;
import com.liepin.pojo.Admin;
import com.liepin.pojo.Article;
import com.liepin.pojo.bo.NewArticleBO;
import com.liepin.service.ArticleService;
import com.liepin.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 文章表 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2022-09-04
 */
@Slf4j
@Service
public class ArticleServiceImpl extends BaseInfoProperties implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public void createArticle(NewArticleBO articleBO) {

        LocalDateTime futureDate = articleBO.getPublishTime();

        Admin currentAdmin = JWTCurrentUserInterceptor.adminUser.get();
        String adminId = currentAdmin.getId();
        String adminName = currentAdmin.getUsername();
        String adminFace = currentAdmin.getFace();

        Article article = new Article();
        BeanUtils.copyProperties(articleBO, article);

        article.setPublishAdminId(adminId);
        article.setPublisher(adminName);
        article.setPublisherFace(adminFace);

        if (futureDate == null) {
            article.setStatus(ArticleStatus.OPEN.type);
        } else {
            article.setPublishTime(futureDate);
            article.setStatus(ArticleStatus.CLOSE.type);
        }

        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());

        articleMapper.insert(article);

        String newArticleId = article.getId();
        // 发送延迟消息到mq，计算发布时间和当前时间的时间差，则为往后延迟的时间
        if (futureDate != null) {

            LocalDateTime nowDate = LocalDateTime.now();

            //long delayTimes = LocalDateUtils.getChronoUnitBetween(nowDate,
            //                                                    futureDate,
            //                                                    ChronoUnit.MILLIS,
            //                                                    true);

            Integer delayTimes = 10 * 1000;
            MessagePostProcessor processor = DelayConfig_Article.setDelayTimes(delayTimes);

            rabbitTemplate.convertAndSend(DelayConfig_Article.EXCHANGE_DELAY_ARTICLE,
                                            DelayConfig_Article.DELAY_DISPLAY_ARTICLE,
                                            newArticleId,
                                            processor);

            log.info("延迟队列消息的发送 - 文章的定时发布：{}", nowDate);
        }
    }

    @Transactional
    @Override
    public void updateArticle(NewArticleBO articleBO) {
        Article article = new Article();
        BeanUtils.copyProperties(articleBO, article);

        article.setId(articleBO.getArticleId());
        article.setUpdateTime(LocalDateTime.now());

        articleMapper.updateById(article);
    }

    @Transactional
    @Override
    public void updateStatus(String articleId, ArticleStatus status) {

        Article pending = new Article();
        pending.setId(articleId);
        pending.setStatus(status.type);
        pending.setUpdateTime(LocalDateTime.now());

        articleMapper.updateById(pending);
    }

    @Override
    public PagedGridResult queryArticleList(ArticleStatus status,
                                            Integer page,
                                            Integer pageSize) {

        PageHelper.startPage(page, pageSize);

        QueryWrapper qw = new QueryWrapper<Article>();
        if (status != null) {
            qw.eq("status", status.type);
        }

        qw.orderByDesc("publish_time");

        List<Article> list = articleMapper.selectList(qw);

        return setterPagedGrid(list, page);
    }

    @Override
    public Article queryArticleDetail(String articleId, ArticleStatus status) {

        QueryWrapper qw = new QueryWrapper<Article>();
        if (status != null) {
            qw.eq("status", status.type);
        }
        qw.eq("id", articleId);

        return articleMapper.selectOne(qw);
    }
}
