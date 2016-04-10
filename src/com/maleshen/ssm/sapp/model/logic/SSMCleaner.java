package com.maleshen.ssm.sapp.model.logic;

import com.maleshen.ssm.sapp.model.ServerChannelsHandler;
import io.netty.channel.Channel;

// This class looks for unactive channels and remove it
// every minute.
public class SSMCleaner implements Runnable {

    private Thread thread;

    public SSMCleaner() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    private void wait(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (ServerChannelsHandler.users != null) {
                for (Channel c : ServerChannelsHandler.users.keySet()){
                    if (!c.isActive()){
                        ServerChannelsHandler.users.remove(c);
                        ServerChannelsHandler.channels.remove(c);
                    }
                }
            }
            //Wait for a minute
            wait(60000);
        }
    }
}