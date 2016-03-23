package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.capp.ClientApp;
import com.maleshen.ssm.entity.User;
import com.maleshen.ssm.template.Flags;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

class SSMConnectorHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg){
        //Auth not complete
        if (!SSMConnector.authenticated) {
            if (msg.equals(Flags.AUTH_BAD)) {
                SSMConnector.answered = true;
            }
            //Auth complete
            if (msg.startsWith(Flags.USER)) {
                SSMConnector.authenticated = true;
                SSMConnector.answered = true;

                //Parsing user from result
                ClientApp.currentUser = User.getFromString(msg);
            }
        }
        //TODO. Chat.
        else {
            System.err.println(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}