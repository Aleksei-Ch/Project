package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Flags;
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

public class SSMConnector {
    public static Boolean authenticated = false;
    static Boolean registered = false;
    static Boolean answered = false;
    static Boolean searchCompleted = false;

    static String HOST = "";
    static int PORT = 0;
    private static Channel ch;
    private static Bootstrap b;
    private static EventLoopGroup group;

    public SSMConnector() throws SSLException { }

    private static void init(){
        ch = null;
        b = new Bootstrap();
        group = new NioEventLoopGroup();
    }

    public static void close(){
        group.shutdownGracefully();
        ch.disconnect();
    }

    private static boolean authenticate(AuthInfo authInfo, Channel ch) throws InterruptedException {

        answered = false;
        ch.writeAndFlush(authInfo.toString() + "\n");

        //Waiting for answer.
        do {
            if (!answered) {
                TimeUnit.SECONDS.sleep(1);
            }
        } while (!answered);

        return authenticated;
    }

    private static boolean registrate(User user, Channel ch) throws InterruptedException {

        answered = false;
        ch.writeAndFlush(user.getRegInfo() + "\n");

        //Waiting for answer.
        do {
            if (!answered) {
                TimeUnit.SECONDS.sleep(1);
            }
        } while (!answered);

        return registered;
    }

    /** Try to establishing connection with server and try auth user
    *
    *   @param authInfo Authentication info
    *   @param host inet address of server
    *   @param port server port
    *   @return  value 0 if auth complete,
    *                   1 if auth failed,
    *                   2 if connection not established
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
                    .handler(new SSMConnectorInitializer(sslCtx));

            ch = b.connect(HOST, PORT).sync().channel();

            if (authenticate(authInfo, ch)) {
                return 0;
            } else {
                group.shutdownGracefully();
                return 1;
            }
//            //Some chat
//            for (; ; ) {
//
//                String line = in.readLine();
//                if (line == null) {
//                    break;
//                }
//                //Unicast msg:
//                //You need type p and login of needed user first.
//                //Example: p logen How are you?
//                //Multicast msg:
//                //Just put your message with m letter first.
//                char[] msg = line.toCharArray();
//                StringBuilder message = new StringBuilder();
//                if (msg.length>1){
//                    for (int i = 2; i < msg.length; i++) {
//                        message.append(msg[i]);
//                    }
//                }
//                if (msg[0] == 'm'){
//                    lastWriteFuture = ch.writeAndFlush(Flags.MULTICAST_MSG+" " + message.toString() + "\n");
//                } else if (msg[0] == 'p') {
//                    lastWriteFuture = ch.writeAndFlush(Flags.UNICAST_MSG + " " + message.toString() + "\n");
//                }
//                // If user typed the 'bye' command, wait until the server closes
//                // the connection.
//                if ("bye".equals(line.toLowerCase())) {
//                    ch.closeFuture().sync();
//                    break;
//                }
//
//            }
//
//            // Wait until all messages are flushed before closing the channel.
//            if (lastWriteFuture != null) {
//                lastWriteFuture.sync();
//            }
        } catch (Exception e) {
            return 2;
        }
    }

    /** Try to establishing connection with server and try registrate user
     *
     *   @param user User reg info
     *   @param host inet address of server
     *   @param port server port
     *   @return  value 0 if registration complete,
     *                   1 if registration failed,
     *                   2 if connection not established
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
                    .handler(new SSMConnectorInitializer(sslCtx));

            ch = b.connect(HOST, PORT).sync().channel();

            if (registrate(user, ch)){
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
    public static void renewData(){
        //Contact list request
        ch.writeAndFlush(Flags.GET_CONTACTS + "\n");

    }

    public static void sendMessage(String message){
        ch.writeAndFlush(message + "\n");
    }

    /** This method formed and write message for server
     *  with keywords for search and receiver login
     *  and wait for server answer
     * @param keywords keywords for search in database
     */
    public static void sendFoundRequest(String keywords) throws InterruptedException {
        searchCompleted = false;

        ch.writeAndFlush(Flags.FOUND_REQ +
                Flags.FOUND_SPLITTER +
//                ClientApp.currentUser.getLogin() +
//                Flags.FOUND_SPLITTER +
                keywords + "\n");

        //Waiting for answer.
        do {
            if (!searchCompleted) {
                TimeUnit.SECONDS.sleep(1);
            }
        } while (!searchCompleted);
    }

    /**
     *
     * @param info : string contains ID both users
     *             pattern: "ID_CURRENT_USER"+USER_SPLITTER+"ID_REMOTE_USER"
     *             without ""
     */
    public static void sendContactsReq(String info){
        ch.writeAndFlush(Flags.SET_CONTACTS + Flags.SETTER_SPLITTER + info + "\n");
    }

}
