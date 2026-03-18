package com.liepin.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 构建简单模式的生产者，发送消息
 */
public class FooProducer {

    public static void main(String[] args) throws Exception {

        // 1. 创建连接工厂以及相关的参数配置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.10.9");
        factory.setPort(5672);
        // 应用：
        //权限隔离：不同的虚拟主机拥有独立的用户权限、交换机、队列。如果不设置，默认就是 /。
        //多租户环境：如果你在同一个 RabbitMQ 实例中为不同的项目或环境（开发、测试）隔离资源，就需要切换到不同的虚拟主机。
        //连接特定资源：确保你的应用连接到正确的虚拟主机，以便访问该主机下定义的队列和交换机。
        factory.setVirtualHost("/");//将连接的虚拟主机设置为根虚拟主机。
        factory.setUsername("admin");
        factory.setPassword("admin");

        // 2. 通过工程创建连接 Connection
        Connection connection = factory.newConnection();

        // 3. 创建管道 Channel
        Channel channel = connection.createChannel();

        // 4. 创建队列 Queue（简单模式不需要交换机Exchange）
        /**
         * queue: 队列名
         * durable: 是否持久化，true：重启之后，队列依然存在，false则不存在
         * exclusive: 是否独占，true：只能有一个消费者监听这个队列，一般设置为false
         * autoDelete: 是否自动删除，true：当没有消费者的时候，则自动删除这个队列
         * arguments: map类型的其他参数
         */
        channel.queueDeclare("hello", true, false, false, null);

        // 5. 向队列发送消息
        /**
         * exchange: 交换机的名称，简单模式下没有，所以直接设置为 ""
         * routingKey: 路由key，映射路径，如果交换机没有，则路由key和队列名保持一致
         * props: 配置参数
         * body: 消息数据
         */
        String msg = "Hello 慕课网~~~";
        channel.basicPublish("", "hello", null, msg.getBytes());

        // 6. 释放资源
        channel.close();
        connection.close();
    }

}
