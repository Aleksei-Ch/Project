package com.maleshen.ssm.sapp.model;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;

public final class ServerConfigurator {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8887"));
    private static final String STOREPASS = "ssmssm";
    private static final String KEYPASS = "simple";

    public void start() throws Exception {

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(getClass().getResource("/resources/cert/ssm.jks").openStream(), STOREPASS.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, KEYPASS.toCharArray());

        SslContext sslCtx = SslContextBuilder.forServer(kmf).build();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerConnectorInitializer(sslCtx));

            b.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
