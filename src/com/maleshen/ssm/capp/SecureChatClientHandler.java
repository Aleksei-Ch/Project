package com.maleshen.ssm.capp;

import com.maleshen.ssm.template.Flags;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SecureChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //Auth not complete
        if (!SecureChatClient.authenticated) {
            if (msg.equals(Flags.AUTH_BAD)) {
                System.err.println("Not logged. Try again.");
                SecureChatClient.answered = true;
            }
            //Auth complete
            if (msg.equals(Flags.AUTH_GOOD)) {
                System.err.println("Logged on.");
                SecureChatClient.authenticated = true;
                SecureChatClient.answered = true;
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