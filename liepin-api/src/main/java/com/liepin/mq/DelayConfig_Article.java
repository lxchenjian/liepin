package com.liepin.mq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 的配置类
 */
@Configuration
public class DelayConfig_Article {

    // 定义交换机的名称
    public static final String EXCHANGE_DELAY_ARTICLE = "exchange_delay_article";

    // 定义队列的名称
    public static final String QUEUE_DELAY_ARTICLE = "queue_delay_article";

    // 统一定义路由key
    public static final String DELAY_DISPLAY_ARTICLE = "delay.display.article";

    // 创建交换机
    @Bean(EXCHANGE_DELAY_ARTICLE)
    public Exchange exchange() {
        return ExchangeBuilder
                    .topicExchange(EXCHANGE_DELAY_ARTICLE)
                    .durable(true)
                    .delayed()              // 设置延迟特性
                    .build();
    }

    // 创建队列
    @Bean(QUEUE_DELAY_ARTICLE)
    public Queue queue() {
//        return new Queue(SMS_QUEUE);
        return QueueBuilder
                .durable(QUEUE_DELAY_ARTICLE)
                .build();
    }

    // 创建绑定关系
    @Bean
    public Binding delayBindingArticle(@Qualifier(EXCHANGE_DELAY_ARTICLE) Exchange exchange,
                                        @Qualifier(QUEUE_DELAY_ARTICLE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("delay.display.*")
                .noargs();
    }

    /**
     * 设置消息属性处理器，目的是设置延迟的时间
     * @param times
     * @return
     */
    public static MessagePostProcessor setDelayTimes(Integer times) {
        return new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置持久属性
                message.getMessageProperties()
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                // 设置延迟的时间，单位毫秒
                message.getMessageProperties().setDelay(times);
                return message;
            }
        };
    }

}
