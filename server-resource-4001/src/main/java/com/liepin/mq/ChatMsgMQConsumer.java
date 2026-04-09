package com.liepin.mq;

import com.liepin.mq.ChatMsgMQConfig;
import com.liepin.pojo.netty.ChatMsg;
import com.liepin.service.ChatMessageService;
import com.liepin.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 聊天消息监听
 */
@Slf4j
@Component
public class ChatMsgMQConsumer {

    @Autowired
    private ChatMessageService chatMessageService;

    @RabbitListener(queues = {ChatMsgMQConfig.QUEUE_MSG})
    public void watchQueue(String payload, Message message) throws Exception {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routingKey = " + routingKey);

        if (routingKey.equalsIgnoreCase(ChatMsgMQConfig.SAVE_MSG_ROUTING_KEY)) {

            String msg = payload;
            log.info("msg = " + msg);

            ChatMsg chatMsg = GsonUtils.stringToBean(msg, ChatMsg.class);
            chatMessageService.saveMsg(chatMsg);
        }
    }

}
