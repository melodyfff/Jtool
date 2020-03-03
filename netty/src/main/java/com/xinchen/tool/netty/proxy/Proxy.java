package com.xinchen.tool.netty.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 * 本地端口转发，也可以转发到指定ip和端口充当代理
 *
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/3/3 21:30
 */
public class Proxy {

    static final int LOCAL_PORT = Integer.parseInt(System.getProperty("localPort", "8180"));
    static final String REMOTE_HOST = System.getProperty("remoteHost", "localhost");
    static final int REMOTE_PORT = Integer.parseInt(System.getProperty("remotePort", "8080"));


    public static void main(String[] args) throws InterruptedException {
        System.err.println("Proxying *:" + LOCAL_PORT + " to " + REMOTE_HOST + ":" + REMOTE_PORT + " ...");

        //Configure the bootstrap

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ProxyInitializer(REMOTE_HOST, REMOTE_PORT))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(LOCAL_PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
