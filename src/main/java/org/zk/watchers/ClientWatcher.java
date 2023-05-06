package org.zk.watchers;

import org.server.Main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class ClientWatcher implements Runnable{
    long delay;

    public ClientWatcher(long delay){this.delay = delay;}
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
        },delay,30_000);
    }
}

class HeartBeat implements Runnable{
    String clientAddress;
    int clientType;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

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
        ByteBuffer ackBuf = ByteBuffer.allocate(1);
        //our task we will time
        Callable<Void> Callable = () -> {
            try {
                channel.receive(ackBuf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        };
        try {
            int retryNum=0;
            while(retryNum < 10) {
                channel.send(buf, new InetSocketAddress(clientAddress.split("/")[0], Integer.parseInt(clientAddress.split("/")[1])));
                Future<Void> task = executorService.submit(Callable);
                //and now the channel waits to receive word back from the client
                try{
                    task.get(500, TimeUnit.MILLISECONDS);
                }catch (TimeoutException | InterruptedException | ExecutionException e){
                    //we didn't get an ack back in time, increment the retry counter and continue
                    retryNum++;
                    continue;
                }
                if ((int) ackBuf.get(0) == -1) {
                    channel.close();
                    return;
                }
                //if we get here we somehow got a value from somewhere else so just retry without incrementing the retry num
            }
            //remove this client
            Main.zkClient.deleteNode(clientAddress);
            //TODO decrement total player count here
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}