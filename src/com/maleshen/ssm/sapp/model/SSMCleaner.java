package com.maleshen.ssm.sapp.model;

import com.maleshen.ssm.sapp.SecureChatServerHandler;
import io.netty.channel.Channel;

// This class looks for unactive channels and remove it
// every minute.
public class SSMCleaner implements Runnable{

    private Thread thread;

    public SSMCleaner(){
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    private void wait(int msec){
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            for (Channel c : SecureChatServerHandler.users.keySet()) {
                if (!c.isActive()) {
                    SecureChatServerHandler.users.remove(c);
                    SecureChatServerHandler.channels.remove(c);
                }
            }
            //Wait for a minute
            wait(60000);
        }
    }
}