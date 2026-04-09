package com.liepin.netty;

import com.liepin.netty.http.HttpHandler;
import com.liepin.netty.http.HttpServerInitializer;
import com.liepin.netty.websocket.WSServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty 服务器
 * 用于构建和启动当前服务
 */
public class NettyServer {

    public static void main(String[] args) throws Exception {

        // 定义主从线程组
        // 定义主（老板）线程组，用于接收客户端连接，但是不做任何处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 定义从（工作）线程组，处理主线程组丢过来的任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建Netty服务器
            ServerBootstrap server = new ServerBootstrap();     // 定义启动类
            // 绑定关系
            server.group(bossGroup, workerGroup)                // 绑定主从线程组
                .channel(NioServerSocketChannel.class)          // 设置nio的双向通道
                .childHandler(new WSServerInitializer());     // 处理器，用于处理workerGroup

            // 启动server，并且绑定端口，同时启动以同步方式进行
            ChannelFuture channelFuture = server.bind(875).sync();
            // 监听关闭的channel
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
