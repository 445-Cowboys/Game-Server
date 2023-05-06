package org.zk.watchers;

import org.checkerframework.checker.units.qual.A;
import org.server.Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

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
                    new Thread(new HeartBeat(client, 0)).start();
                }
                //do the same thing for the players in the game rooms
                List<String>waitingGamePlayers0 = Main.zkClient.getWaitingGameClients(0);
                for(String client:waitingGamePlayers0){
                    //spin up a heartbeat thread that will attempt to ping the client in the list of clients
                    new Thread(new HeartBeat(client, 1)).start();
                }

                List<String>waitingGamePlayers1 = Main.zkClient.getWaitingGameClients(1);
                for(String client:waitingGamePlayers1){
                    //spin up a heartbeat thread that will attempt to ping the client in the list of clients
                    new Thread(new HeartBeat(client, 1)).start();
                }

                List<String>waitingGamePlayers2 = Main.zkClient.getWaitingGameClients(2);
                for(String client:waitingGamePlayers2){
                    //spin up a heartbeat thread that will attempt to ping the client in the list of clients
                    new Thread(new HeartBeat(client, 1)).start();
                }

                List<String>activeGamePlayers0 = Main.zkClient.getGameClients(0);
                for(String client:activeGamePlayers0){
                    //spin up a heartbeat thread that will attempt to ping the client in the list of clients
                    new Thread(new HeartBeat(client, 1)).start();
                }

                List<String>activeGamePlayers1 = Main.zkClient.getGameClients(1);
                for(String client:activeGamePlayers1){
                    //spin up a heartbeat thread that will attempt to ping the client in the list of clients
                    new Thread(new HeartBeat(client, 1)).start();
                }

                List<String>activeGamePlayers2 = Main.zkClient.getGameClients(2);
                for(String client:activeGamePlayers2){
                    //spin up a heartbeat thread that will attempt to ping the client in the list of clients
                    new Thread(new HeartBeat(client, 1)).start();
                }

            }
        },15_000,30_000);
    }
}

class HeartBeat implements Runnable{
    String clientAddress;
    int clientType;

    public HeartBeat(String clientAddress, int clientType){
        this.clientAddress=clientAddress;
        this.clientType=clientType;
    }

    @Override
    public void run() {
        DatagramChannel channel;
        try {
            channel = DatagramChannel.open().bind(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put((byte) -1);
        buf.flip();
        //I think we agreed the port would be 7086?
        try {
            for(;;) {
                channel.send(buf, new InetSocketAddress(clientAddress, 7086));

                //and now the channel waits to receive word back from the client
                ByteBuffer ackBuf = ByteBuffer.allocate(1);
                channel.receive(ackBuf);
                if ((int) ackBuf.get(0) == -1) {
                    return;
                }
                //if it is not -1, something really weird happened so we should initiate the loop and send the heart beat again


                //Note: tomorrwo I'll put this all in a future and it'll delete the client if they are not found
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
