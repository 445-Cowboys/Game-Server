package org.zk.watchers;

import java.util.Timer;
import java.util.TimerTask;

public class ClientWatcher implements Runnable{
    Timer timer = new Timer();
    @Override
    public void run() {
        //Every 30 seconds, send a heartbeat ack to all the clients
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Sending Client Heartbeats...");
                //get the list of live clients, spin up a heartbeat thread for each of them

            }
        },15_000,30_000);
    }
}
