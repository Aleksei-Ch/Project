package com.maleshen.ssm.capp;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.template.Flags;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public final class SecureChatClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));
    static Boolean authenticated = false;
    static Boolean answered = false;

    private static void authenticate(Channel ch) throws InterruptedException {
        while (!authenticated) {
            answered = false;
            System.out.print("Login: ");
            Scanner in = new Scanner(System.in);
            String usename = in.nextLine();
            System.out.print("Password: ");
            String pass = in.nextLine();

            ch.writeAndFlush((new AuthInfo(usename, pass)).toString() + "\n");

            //Waiting for answer.
            //It's a kostyl.
            TimeUnit.SECONDS.sleep(2);
            while (!answered) {
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new SecureChatClientInitializer(sslCtx));

            // Start the connection attempt.
            Channel ch = b.connect(HOST, PORT).sync().channel();

            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            //Auth
            authenticate(ch);


            //Some chat
            for (; ; ) {

                String line = in.readLine();
                if (line == null) {
                    break;
                }
                //Unicast msg:
                //You need type p and login of needed user first.
                //Example: p logen How are you?
                //Multicast msg:
                //Just put your message with m letter first.
                char[] msg = line.toCharArray();
                StringBuilder message = new StringBuilder();
                if (msg.length>1){
                    for (int i = 2; i < msg.length; i++) {
                        message.append(msg[i]);
                    }
                }
                if (msg[0] == 'm'){
                    lastWriteFuture = ch.writeAndFlush(Flags.MULTICAST_MSG+" " + message.toString() + "\n");
                } else if (msg[0] == 'p') {
                    lastWriteFuture = ch.writeAndFlush(Flags.UNICAST_MSG + " " + message.toString() + "\n");
                }
                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }

            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }
}