package com.liepin.netty.mq;

import com.liepin.pojo.netty.ChatMsg;
import com.liepin.utils.GsonUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MessagePublisher {

    // 定义交换机
    public static final String EXCHANGE_MSG = "exchange_chat_msg";
    // 定义队列
    public static final String QUEUE_MSG = "queue_chat_msg";
    // 定义路由key
    public static final String SAVE_MSG_ROUTING_KEY = "imooc.chat.msg.save.do";

    public static void saveMessageByMQ(String msg) throws Exception {

        // 1. 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 1.1 设置连接参数
        factory.setHost("192.168.1.122");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("imooc");
        factory.setPassword("imooc");

        // 1.2 建立连接
        Connection connection = factory.newConnection();

        // 2. 创建通道channel
        Channel channel = connection.createChannel();

        // 3. 定义队列
        channel.queueDeclare(QUEUE_MSG,
                            true,
                            false,
                            false,
                            null);
        // 4. 发送消息
        channel.basicPublish(EXCHANGE_MSG,
                            SAVE_MSG_ROUTING_KEY,
                            null,
                            msg.getBytes());

        // 5. 关闭通道和连接
        channel.close();
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        //saveMessageByMQ("Send a chat msg by rabbitmq~~~  another  ");

        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        String msg = "Send a chat msg by rabbitmq~~~  new ";
        connectUtils.sendMsg(msg, EXCHANGE_MSG, SAVE_MSG_ROUTING_KEY);
    }

    public static void sendMsgToSave(ChatMsg msg) throws Exception {
        RabbitMQConnectUtils connectUtils = new RabbitMQConnectUtils();
        connectUtils.sendMsg(GsonUtils.object2String(msg),
                            EXCHANGE_MSG,
                            SAVE_MSG_ROUTING_KEY);
    }


}
