package com.liepin.mq;

import com.liepin.mq.DelayConfig_Article;
import com.liepin.base.BaseInfoProperties;
import com.liepin.enums.ArticleStatus;
import com.liepin.service.ArticleService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class RabbitMQDelayConsumer_Article extends BaseInfoProperties {

    @Autowired
    private ArticleService articleService;

    @RabbitListener(queues = {DelayConfig_Article.QUEUE_DELAY_ARTICLE})
    public void watchQueue(Message message, Channel channel) throws Exception {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routingKey = " + routingKey);

        String msg = new String(message.getBody());
        log.info("msg = " + msg);
        log.info("当前时间为：" + LocalDateTime.now());

        if (routingKey.equalsIgnoreCase(DelayConfig_Article.DELAY_DISPLAY_ARTICLE)) {
            log.info("10秒后监听到延迟队列");

            String articleId = msg;
            articleService.updateStatus(articleId, ArticleStatus.OPEN);
        }
    }
}
