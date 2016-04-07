package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.model.security.MsgManager;
import com.maleshen.ssm.capp.view.FindAddContactsController;
import com.maleshen.ssm.capp.view.MainSceneController;
import com.maleshen.ssm.entity.ArrayListExt;
import com.maleshen.ssm.entity.Message;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Headers;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.collections.FXCollections;

class ClientChanelHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        //Auth not complete
        if (!ClientConnector.authenticated) {

            if (msg.startsWith(Headers.AUTH_REG_BAD)) {
                ClientConnector.answered = true;
            }
            //Auth or registration complete
            else if (msg.startsWith(Headers.AUTH_REG_GOOD)) {
                ClientConnector.authenticated = true;
                ClientConnector.answered = true;
                ClientConnector.registered = true;

                msg = MsgManager.getMsgData(Headers.AUTH_REG_GOOD, msg);

                //Parsing user from result
                ClientApp.currentUser = User.getFromString(msg);
            }
        }
        // Chat logic.
        else {
            //First time we need to get contact list. So look for them
            if (msg.startsWith(Headers.CONTACT_LIST)) {

                msg = MsgManager.getMsgData(Headers.CONTACT_LIST, msg);
                //Init
                ClientApp.contactList = FXCollections.observableArrayList();
                //Fill
                ClientApp.contactList.addAll(ArrayListExt.getFromString(msg));
            }
            //Simple message for me
            else if (msg.startsWith(Headers.MSG)) {
                msg = MsgManager.getMsgData(Headers.MSG, msg);
                MainSceneController.getMessage(Message.getFromString(msg));
            }
            // Look for message with search users results
            else if (msg.startsWith(Headers.FOUND_USERS)) {
                ClientConnector.searchCompleted = true;

                msg = MsgManager.getMsgData(Headers.FOUND_USERS, msg);

                FindAddContactsController.getResult(ArrayListExt.getFromString(msg));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}