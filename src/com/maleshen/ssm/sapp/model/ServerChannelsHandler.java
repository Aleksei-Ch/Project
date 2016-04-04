package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.entity.ArrayListExt;
import com.maleshen.ssm.entity.AuthInfo;
import com.maleshen.ssm.entity.Message;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.sapp.model.logic.AuthenticationImpl;
import com.maleshen.ssm.sapp.model.logic.DBWorker;
import com.maleshen.ssm.template.Flags;
import com.maleshen.ssm.template.Headers;
import com.maleshen.ssm.template.MsgManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ServerChannelsHandler extends SimpleChannelInboundHandler<String>{

    //All connections
    public static volatile ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //Authorized users
    public static volatile Map<Channel, User> users = new HashMap<>();

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

    private void authOrReg(Channel c, String message) throws SQLException, ClassNotFoundException {
        //Look for authentication request
        if (message.startsWith(Headers.AUTH)) {

            message = MsgManager.getMsgData(Headers.AUTH, message);

            //Trying to get auth
            AuthInfo authInfo = AuthInfo.getFromString(message);
            User user = authInfo == null ? null : (new AuthenticationImpl()).getAuthentication(authInfo);

            if (user != null) {
                users.put(c, user);
                c.writeAndFlush(MsgManager.createMsg(Headers.AUTH_REG_GOOD, user.toString()));

                //Look for not delivered msgs
                deliverLost(c, user.getLogin());
            } else {
                c.writeAndFlush(MsgManager.createMsg(Headers.AUTH_REG_BAD));
            }
        }

        //If message is not authinfo - it must be registration info.
        if (message.startsWith(Headers.REG)){

            message = MsgManager.getMsgData(Headers.REG, message);

            //Parse message for full reginfo
            User userFromReq = User.getUserFromRegInfo(message);
            if (userFromReq != null) {
                try {
                    User newUser = DBWorker.registerUser(userFromReq);
                    if (newUser != null) {
                        users.put(c, newUser);
                        c.writeAndFlush(MsgManager.createMsg(Headers.AUTH_REG_GOOD, newUser.toString()));
                    } else {
                        c.writeAndFlush(MsgManager.createMsg(Headers.AUTH_REG_BAD));
                    }
                } catch (Exception e) {
                    c.writeAndFlush(MsgManager.createMsg(Headers.AUTH_REG_BAD));
                }
            }
        }
    }

    private void sendContacts(Channel c) throws SQLException, ClassNotFoundException {

        ArrayListExt<User> contacts = DBWorker.getContactList(
                users.get(c).getId());

        c.writeAndFlush(MsgManager.createMsg(Headers.CONTACT_LIST, contacts.toString()));
    }

    private void redirectUnicastMsg(String message) throws SQLException, ClassNotFoundException {

        message = MsgManager.getMsgData(Headers.MSG, message);

        Message messageEntity = Message.getFromString(message);
        boolean later = true;

        assert messageEntity != null;
        if (messageEntity.getToUser() != null) {
            for (User u : users.values()) {
                if (u.getLogin().equals(messageEntity.getToUser())) {
                    for (Channel c : users.keySet()) {
                        if (users.get(c).getLogin().equals(u.getLogin())) {
                            if (!c.isActive()){
                                users.remove(c);
                                break;
                            }
                            c.writeAndFlush(MsgManager.createMsg(Headers.MSG, messageEntity.toString()));
                            later = false;
                            break;
                        }
                    }
                    break;
                }
            }
            if (later){
                DBWorker.putMessage(messageEntity);
            }
        }
    }

    private void deliverLost(Channel c, String login) throws SQLException, ClassNotFoundException {
        ObservableList<Message> messages = DBWorker.getMessagesForUser(login);

        if (messages.size() > 0){
            for (Message m : messages){
                c.writeAndFlush(MsgManager.createMsg(Headers.MSG, m.toString()));
            }
        }
    }

    private void foundAndAnswer(Channel c, String message) throws SQLException, ClassNotFoundException {
        if (c.isActive()){
            message = MsgManager.getMsgData(Headers.FOUND_USERS, message);

            StringBuilder keywords = new StringBuilder();
            keywords.append(message.split(Flags.FOUND_SPLITTER)[0]);

            if (message.split(Flags.FOUND_SPLITTER).length > 1){
                for (int i = 1; i < message.split(Flags.FOUND_SPLITTER).length; i++){
                    keywords.append(message.split(Flags.FOUND_SPLITTER)[i]);
                }
            }

            c.writeAndFlush(MsgManager.createMsg(Headers.FOUND_USERS,
                    DBWorker.foundUsersByKeyword(keywords.toString()).toString()));
        }
    }

    private void setContacts(String message){
        if (message.startsWith(Headers.ADD_CONTACT)) {
            try {
                message = MsgManager.getMsgData(Headers.ADD_CONTACT, message);

                int firstID = Integer.parseInt(message.split(Flags.USER_SPLITTER)[0]);
                int secondID = Integer.parseInt(message.split(Flags.USER_SPLITTER)[1]);

                DBWorker.setContacts(firstID, secondID);
                DBWorker.setContacts(secondID, firstID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (message.startsWith(Headers.REM_CONTACT)){
            try {
                message = MsgManager.getMsgData(Headers.REM_CONTACT, message);

                int firstID = Integer.parseInt(message.split(Flags.USER_SPLITTER)[0]);
                int secondID = Integer.parseInt(message.split(Flags.USER_SPLITTER)[1]);

                DBWorker.removeContacts(firstID, secondID);
                DBWorker.removeContacts(secondID, firstID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUser(Channel c, String message) throws SQLException, ClassNotFoundException {

        message = MsgManager.getMsgData(Headers.UPDATE_ME, message);

        DBWorker.updateUser(User.getUserFromUpdateInfo(message));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        //Look if chanel not authorized
        //Then do authorization or registration user.
        if (!users.keySet().contains(ctx.channel())) {
            authOrReg(ctx.channel(), msg);
        } else

        //Look for request List contacts
        if (msg.startsWith(Headers.CONTACT_LIST)) {
            sendContacts(ctx.channel());
        } else

        if (msg.startsWith(Headers.MSG)) {
            redirectUnicastMsg(msg);
        } else

        if (msg.startsWith(Headers.FOUND_USERS)){
            foundAndAnswer(ctx.channel(), msg);
        } else

        if (msg.startsWith(Headers.ADD_CONTACT) || msg.startsWith(Headers.REM_CONTACT)){
            setContacts(msg);
        } else

        if (msg.startsWith(Headers.UPDATE_ME)){
            updateUser(ctx.channel(), msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}