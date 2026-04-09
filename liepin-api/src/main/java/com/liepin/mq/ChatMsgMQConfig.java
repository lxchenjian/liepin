package com.liepin.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 的配置类
 */
@Configuration
public class ChatMsgMQConfig {

    // 定义交换机
    public static final String EXCHANGE_MSG = "exchange_chat_msg";
    // 定义队列
    public static final String QUEUE_MSG = "queue_chat_msg";
    // 定义路由key
    public static final String SAVE_MSG_ROUTING_KEY = "imooc.chat.msg.save.do";

    // 创建交换机
    @Bean(EXCHANGE_MSG)
    public Exchange exchange() {
        return ExchangeBuilder
                    .topicExchange(EXCHANGE_MSG)
                    .durable(true)
                    .build();
    }

    // 创建队列
    @Bean(QUEUE_MSG)
    public Queue queue() {
        return new Queue(QUEUE_MSG);
    }

    // 创建绑定关系
    @Bean
    public Binding chatMsgMQBinding(@Qualifier(EXCHANGE_MSG) Exchange exchange,
                              @Qualifier(QUEUE_MSG) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(SAVE_MSG_ROUTING_KEY)
                .noargs();
    }

}
