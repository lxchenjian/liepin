package com.liepin.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 初始化器，channel注册后，会执行里面的相应的初始化方法
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        // 通过SocketChannel获得对应的管道
        ChannelPipeline pipeline = channel.pipeline();

        /**
         * 通过管道添加handler
         */

        // HttpServerCodec 是由netty自己提供的助手类，可以理解为拦截器
        // 当请求到达服务端，需要做解码，响应给到客户端做编码
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());

        // 注册（添加）自定义的助手类，返回 “hello netty~”
        pipeline.addLast("httpHandler", new HttpHandler());

    }
}
