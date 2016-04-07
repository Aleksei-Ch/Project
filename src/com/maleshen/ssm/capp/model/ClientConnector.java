package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.capp.model.security.MsgManager;
import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Headers;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;

public class ClientConnector {
    public static Boolean authenticated = false;
    static Boolean registered = false;
    static Boolean answered = false;
    static Boolean searchCompleted = false;

    static String HOST = "";
    static int PORT = 0;
    private static Channel ch;
    private static Bootstrap b;
    private static EventLoopGroup group;

    public ClientConnector() throws SSLException {
    }

    private static void init() {
        ch = null;
        b = new Bootstrap();
        group = new NioEventLoopGroup();
    }

    public static void close() {
        group.shutdownGracefully();
        ch.disconnect();
    }

    private static boolean authentication(AuthInfo authInfo, Channel ch) throws InterruptedException {

        answered = false;
        ch.writeAndFlush(MsgManager.createMsg(Headers.AUTH, authInfo.toString()));

        //Waiting for answer.
        do {
            if (!answered) {
                TimeUnit.SECONDS.sleep(1);
            }
        } while (!answered);

        return authenticated;
    }

    private static boolean registration(User user, Channel ch) throws InterruptedException {

        answered = false;
        ch.writeAndFlush(MsgManager.createMsg(Headers.REG, user.getRegInfo()));

        //Waiting for answer.
        do {
            if (!answered) {
                TimeUnit.SECONDS.sleep(1);
            }
        } while (!answered);

        return registered;
    }

    /**
     * Try to establishing connection with server and try auth user
     *
     * @param authInfo Authentication info
     * @param host     inet address of server
     * @param port     server port
     * @return value 0 if auth complete,
     * 1 if auth failed,
     * 2 if connection not established
     */
    public static int establishingConnection(AuthInfo authInfo, String host, int port) throws Exception {

        HOST = host;
        PORT = port;
        init();

        try {
            SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientConnectorInitializer(sslCtx));

            ch = b.connect(HOST, PORT).sync().channel();

            if (authentication(authInfo, ch)) {
                return 0;
            } else {
                group.shutdownGracefully();
                return 1;
            }
        } catch (Exception e) {
            return 2;
        }
    }

    /**
     * Try to establishing connection with server and try registration user
     *
     * @param user User reg info
     * @param host inet address of server
     * @param port server port
     * @return value 0 if registration complete,
     * 1 if registration failed,
     * 2 if connection not established
     */
    public static int establishingConnection(User user, String host, int port) throws Exception {

        HOST = host;
        PORT = port;
        init();

        try {
            SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientConnectorInitializer(sslCtx));

            ch = b.connect(HOST, PORT).sync().channel();

            if (registration(user, ch)) {
                return 0;
            } else {
                group.shutdownGracefully();
                return 1;
            }
        } catch (Exception e) {
            return 2;
        }
    }

    // Autorenew contact list and other shit.
    public static void renewData() {
        //Contact list request
        ch.writeAndFlush(MsgManager.createMsg(Headers.CONTACT_LIST));
    }

    public static void sendMessage(String message) {
        ch.writeAndFlush(MsgManager.createMsg(Headers.MSG, message));
    }

    /**
     * This method formed and write message for server
     * with keywords for search and receiver login
     * and wait for server answer
     *
     * @param keywords keywords for search in database
     */
    public static void sendFoundRequest(String keywords) throws InterruptedException {
        searchCompleted = false;

        ch.writeAndFlush(MsgManager.createMsg(Headers.FOUND_USERS, keywords));

        //Waiting for answer.
        do {
            if (!searchCompleted) {
                TimeUnit.SECONDS.sleep(1);
            }
        } while (!searchCompleted);
    }

    /**
     * @param info : string contains ID both users
     */
    public static void sendContactsReq(String info) {
        ch.writeAndFlush(MsgManager.createMsg(Headers.ADD_CONTACT, info));
    }

    public static void sendContactRemReq(String info) {
        ch.writeAndFlush(MsgManager.createMsg(Headers.REM_CONTACT, info));
    }

    public static void updateMe(User user) {
        ch.writeAndFlush(MsgManager.createMsg(Headers.UPDATE_ME, user.getUpdateInfo()));
    }

}
