package com.maleshen.ssm.sapp;

import com.maleshen.ssm.entity.ArrayListExt;
import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.SSMAuthImpl;
import com.maleshen.ssm.sapp.model.SSMDataBaseWorker;
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

import javax.jws.soap.SOAPBinding;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SecureChatServerHandler extends SimpleChannelInboundHandler<String>{

    //All connections
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //Authorized users
    static final Map<Channel, User> users = new HashMap<>();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        channels.add(ctx.channel());
                    }
                });
    }

    private void authOrReg(ChannelHandlerContext ctx, String msg) throws SQLException, ClassNotFoundException {
        //Look for authentication request
        if (msg.startsWith(Flags.AUTH_REQ)) {
            //Trying to get auth
            User user = AuthInfo.getFromString(msg) != null ?
                    (new SSMAuthImpl()).getAuthentication(AuthInfo.getFromString(msg)) : null;

            if (user != null) {
                users.put(ctx.channel(), user);
                ctx.channel().writeAndFlush(user.toString() + "\n");
            } else {
                ctx.channel().writeAndFlush(Flags.AUTH_BAD + "\n");
            }
        }

        //If msg is not authinfo - it must be registration info.
        if (msg.startsWith(Flags.REGME)) {

            //Parse msg for full reginfo
            User userFromReq = User.getUserFromRegInfo(msg);
            if (userFromReq != null) {
                User newUser = SSMDataBaseWorker.registerUser(userFromReq);
                ctx.channel().writeAndFlush(newUser != null ?
                        newUser.toString() + "\n" : Flags.REGFAULT + "\n");
            }
        }
        //Not authorized, not authInfo, not regInfo - broken msg.
        else {
            ctx.channel().writeAndFlush(Flags.MSG_BROKEN + "\n");
        }
    }

    private void sendContacts(ChannelHandlerContext ctx) throws SQLException, ClassNotFoundException {

        ArrayListExt<User> contacts = (ArrayListExt<User>) SSMDataBaseWorker.getContactList(
                users.get(ctx.channel()).getId());

        ctx.channel().writeAndFlush(contacts.toString() + "\n");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        //Look if chanel not authorized
        //Then do authorization or registration user.
        if (!users.keySet().contains(ctx.channel())) { authOrReg(ctx, msg); }

        //Some work logic
        else {
            //Look for request List contacts
            if (msg.startsWith(Flags.GET_CONTACTS)){
                sendContacts(ctx);
            }

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