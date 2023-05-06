package org.server;

import org.zk.watchers.LeaderChangeListener;
import org.zk.watchers.ZookeeperClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Main {
    public static ZookeeperClient zkClient;
    public static void main(String[] args) throws IOException, InterruptedException {
        //args[0] holds the IP address of the zk server (it will always be on port 2181)
        //initiate the zookeeper watchers
        zkClient=new ZookeeperClient(args[0], args[1]);
        zkClient.registerLeaderChangeWatcher("/election", new LeaderChangeListener());
        //the value at args[1] is the IP and port that the server is listening on
        zkClient.electLeader();
        //This forever loop will always listen for input from a client, it will never stop unless the server crashes
        //unceremoniously
        //arg[1] has our IP address (will always be listening on port 7806)
//        zkClient.getWriteLock("/election");
//        Thread.sleep(90000);
//        zkClient.releaseWriteLock("/election");

//        zkClient.getReadLock("/election");
//        Thread.sleep(60000);
//        zkClient.releaseReadLock("/election");
        DatagramChannel channel = DatagramChannel.open().bind(new InetSocketAddress(args[1], 7086));
        for(;;){
            //listen for messages sent to this server, pass along the message to a request handler
            ByteBuffer data = ByteBuffer.allocate(1024);
            new Thread(new RequestHandler(channel.receive(data), data)).start();
        }
    }
}