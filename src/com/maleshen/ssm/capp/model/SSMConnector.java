package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.entity.AuthInfo;
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
    static Boolean authenticated = false;
    static Boolean answered = false;
    static String HOST = "";
    static int PORT = 0;
    private static Channel ch;
    private static Bootstrap b;
    private static EventLoopGroup group;

    public SSMConnector() throws SSLException {
    }

    private static void init(){
        ch = null;
        b = new Bootstrap();
        group = new NioEventLoopGroup();
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

    /* This method try to establishing connection with server
    *
    *   @param AuthInfo Authentication info
    *   @param String host inet address of server
    *   @param int port server port
    *   @Returned value 0 if auth complete,
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

            // Read commands from the stdin.
//            ChannelFuture lastWriteFuture = null;
//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            //Authenticate
            if (authenticate(authInfo, ch)) {

                //Ok, in this point we need to do chat.
                return 0;
            } else {
                group.shutdownGracefully();
                ch.close();
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
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }
}
