package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.security.MsgManager;
import com.maleshen.ssm.capp.view.AuthRegSceneController;
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

import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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
    private static TrustManagerFactory tmf;

    public ClientConnector() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
    }

    private static void init() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        ch = null;
        b = new Bootstrap();
        group = new NioEventLoopGroup();

        char[] passphrase = "ssmssm".toCharArray();

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(ClientApp.class.getResource("/resources/cert/ssmclientts.jks").openStream(), passphrase);

        tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);
    }

    public static void close() {
        group.shutdownGracefully();
        ch.disconnect();
    }

    private static int authentication(AuthInfo authInfo, Channel ch) throws InterruptedException {

        answered = false;
        ch.writeAndFlush(MsgManager.createMsg(Headers.AUTH, authInfo.toString()));

        //Waiting for answer.
        do {
            if (AuthRegSceneController.sslErr)
                return 2;
            if (!answered) {
                TimeUnit.SECONDS.sleep(1);
            }
        } while (!answered);

        return authenticated ? 0 : 1;
    }

    private static int registration(User user, Channel ch) throws InterruptedException {

        answered = false;
        ch.writeAndFlush(MsgManager.createMsg(Headers.REG, user.getRegInfo()));

        //Waiting for answer.
        do {
            if (AuthRegSceneController.sslErr)
                return 2;
            if (!answered)
                TimeUnit.SECONDS.sleep(1);
        } while (!answered);

        return registered ? 0 : 1;
    }


    /**
     * Try to establishing connection with server and try auth user
     *
     * @param authInfo Authentication info
     * @param host     inet address of server
     * @param port     server port
     * @return value 0 if auth complete,
     * 1 if auth failed,
     * 2 if ssl connection wrong
     * -1 if not connected
     */
    public static int establishingConnection(AuthInfo authInfo, String host, int port) throws Exception {

        HOST = host;
        PORT = port;
        init();

        try {
            SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(tmf).build();

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientConnectorInitializer(sslCtx));

            ch = b.connect(HOST, PORT).sync().channel();

            int code = authentication(authInfo, ch);

            if (code == 0) {
                return 0;
            } else {
                group.shutdownGracefully();
                return code;
            }
        } catch (Exception e) {
            return -1;
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
     * 2 if ssl connection wrong
     * -1 if not connected
     */
    public static int establishingConnection(User user, String host, int port) throws Exception {

        HOST = host;
        PORT = port;
        init();

        try {
            SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(tmf).build();

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientConnectorInitializer(sslCtx));

            ch = b.connect(HOST, PORT).sync().channel();

            int code = registration(user, ch);

            if (code == 0) {
                return 0;
            } else {
                group.shutdownGracefully();
                return code;
            }
        } catch (Exception e) {
            return -1;
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
