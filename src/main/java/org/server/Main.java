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
        //args[0] holds the IP
        //args[1] holds the Port number
//        DatagramChannel channel = DatagramChannel.open().bind(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
        //initiate the zookeeper watchers
        zkClient=new ZookeeperClient(args[0]);
        zkClient.registerLeaderChangeWatcher("/election", new LeaderChangeListener());
        //the value at args[1] is the IP and port that the server is listening on
        zkClient.electLeader();
        //This forever loop will always listen for input from a client, it will never stop unless the server crashes
        //unceremoniously
        //now get the lock
//        zkClient.getReadLock("/election");
//        Thread.sleep(50000);
//        zkClient.releaseReadLock("/election");

        zkClient.getWriteLock("/election");
        Thread.sleep(15000);
        zkClient.releaseWriteLock("/election");
        for(;;){
            //listen for messages sent to this server, pass along the message to a request handler
//            ByteBuffer data = ByteBuffer.allocate(1024);
//            new Thread(new RequestHandler(channel.receive(data), data)).start();
        }
    }
}