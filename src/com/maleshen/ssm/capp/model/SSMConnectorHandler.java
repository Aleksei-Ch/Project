package com.maleshen.ssm.capp.model;

import com.maleshen.ssm.template.Flags;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

class SSMConnectorHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //Auth not complete
        if (!SSMConnector.authenticated) {
            if (msg.equals(Flags.AUTH_BAD)) {
                SSMConnector.answered = true;
            }
            //Auth complete
            if (msg.equals(Flags.AUTH_GOOD)) {
                SSMConnector.authenticated = true;
                SSMConnector.answered = true;
            }
        }
        //All good, get chat
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