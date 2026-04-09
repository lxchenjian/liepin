package com.liepin.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 初始化器，channel注册后，会执行里面的相应的初始化方法
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        // 通过SocketChannel获得对应的管道
        ChannelPipeline pipeline = channel.pipeline();

        /**
         * 通过管道添加handler
         */

        // HttpServerCodec 是由netty自己提供的助手类，可以理解为拦截器
        // 当请求到达服务端，需要做解码，响应给到客户端做编码
        pipeline.addLast(new HttpServerCodec());

        // 对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());

        // 几乎在netty编程中，都会使用到这个handler
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));

        // ============================== 以上是用于支持http协议 ==============================


        // ============================== 增加心跳机制的支持 start ==============================

        // 针对客户端，如果在1分钟时没有向服务端发送读写心跳（all），则主动断开
        // 如果读空闲或者写空闲，则不处理
        pipeline.addLast(new IdleStateHandler(
                                8,
                                10,
                                600));

        // 自定义空闲状态的处理器
        pipeline.addLast(new HeartBeatHandler());

        // ============================== 增加心跳机制的支持 end ==============================


        // ============================== 以下是用于支持websocket协议 ==============================

        /**
         * WebSocket 服务器处理的协议，用于指定给客户端连接访问的路由：/ws
         * 本handler会帮你处理一些繁重的事
         * 会处理握手动作：handshaking(close ping pong) ping + pong = 心跳
         * 对于WebSocket来讲，都是以frames来进行传输，不同的数据类型对应的frames也不同
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        // 自定义处理器
        pipeline.addLast(new ChatHandler());
    }


}
