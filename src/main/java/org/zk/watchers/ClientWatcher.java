package org.zk.watchers;

import org.server.Main;

import java.util.List;
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
                //we don't need to lock or anything here, if we try pinging a client who is gone, then we'll end up just trying to remove them
                //from the list of clients and will find that they are already gone
                List<String> waitingLobbyClients = Main.zkClient.getWaitingClients();
                for(String client:waitingLobbyClients){
                    //spin up a heartbeat thread that will attempt to ping the client in the list of clients
                }

            }
        },15_000,30_000);
    }
}
