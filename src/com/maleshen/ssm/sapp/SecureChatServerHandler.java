package com.maleshen.ssm.sapp;

import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.SSMAuthImpl;
import com.maleshen.ssm.template.Flags;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;

public class SecureChatServerHandler extends ChannelInboundHandlerAdapter {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    static final Map<Channel, User> users = new HashMap<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        //First time Hello msg

//                        ctx.writeAndFlush(
//                                "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
//                        ctx.writeAndFlush(
//                                "Your session is protected by " +
//                                        ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
//                                        " cipher suite.\n");

                        channels.add(ctx.channel());
                    }
                });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgg) throws Exception {
        //Look if authorized
        String msg = (String) msgg;
        if (!users.keySet().contains(ctx.channel())) {
            //Trying to get auth
            AuthInfo authInfo = AuthInfo.getFromString((String) msgg);

            //Some Auth logic
            if (authInfo != null) {
                User user = (new SSMAuthImpl()).getAuthentication(authInfo);
                if (user != null) {
                    users.put(ctx.channel(), user);
                    ctx.channel().writeAndFlush(user.toString() + "\n");
                } else {
                    ctx.channel().writeAndFlush(Flags.AUTH_BAD + "\n");
                }
            }
        }

        //TODO GET CHAT HERE.
        else {
            //Look for unicast/multicast flag in msg
            if (msg.split(" ")[0].equals(Flags.UNICAST_MSG)) {
                for (Channel c : users.keySet()) {
                    if (users.get(c).getLogin().equals(msg.split(" ")[1])) {
                        StringBuilder message = new StringBuilder();
                        if (msg.split(" ").length > 2) {
                            for (int i = 2; i < msg.split(" ").length; i++) {
                                message.append(msg.split(" ")[i])
                                        .append(i != msg.split(" ").length - 1 ? " " : "\n");
                            }
                        }
                        c.writeAndFlush("Private message from " + users.get(ctx.channel()).getLogin() + ": " + message.toString());
                    }
                }
            } else if (msg.split(" ")[0].equals(Flags.MULTICAST_MSG)) {
                for (Channel c : users.keySet()) {
                        StringBuilder message = new StringBuilder();
                        if (msg.split(" ").length > 0) {
                            for (int i = 1; i < msg.split(" ").length; i++) {
                                message.append(msg.split(" ")[i])
                                        .append(i != msg.split(" ").length - 1 ? " " : "\n");
                            }
                        }
                        c.writeAndFlush(users.get(ctx.channel()).getLogin() + " to all: " + message.toString());
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}