package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.capp.view.MainSceneController;
import com.maleshen.ssm.entity.ArrayListExt;
import com.maleshen.ssm.entity.Message;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Flags;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.collections.FXCollections;

class SSMConnectorHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg){
        //Auth not complete
        if (!SSMConnector.authenticated) {

            if (msg.startsWith(Flags.AUTH_BAD) ||
                    msg.startsWith(Flags.REGFAULT)) {
                SSMConnector.answered = true;
            }

            //Auth or registration complete
            if (msg.startsWith(Flags.USER)) {
                SSMConnector.authenticated = true;
                SSMConnector.answered = true;
                SSMConnector.registered = true;

                //Parsing user from result
                ClientApp.currentUser = User.getFromString(msg);
            }
        }
        // Chat logic.
        else {
            //First time we need to get contact list. So look for them
            if (msg.startsWith(Flags.GET_CONTACTS)){
                //Init
                ClientApp.contactList = FXCollections.observableArrayList();
                //Fill
                ClientApp.contactList.addAll(ArrayListExt.getFromString(msg));
            }
            if (msg.startsWith(Flags.UNICAST_MSG)){
                MainSceneController.getMessage(Message.getFromString(msg));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}