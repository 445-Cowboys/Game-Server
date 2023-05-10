package org.server;

import org.zk.watchers.LeaderChangeListener;
import org.zk.watchers.ZookeeperClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.security.GeneralSecurityException;

public class Main {
    public static ZookeeperClient zkClient;
    public static AEAD keyRoom0;

    static {
        try {
            keyRoom0 = new AEAD();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static AEAD keyRoom1;

    static {
        try {
            keyRoom1 = new AEAD();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static AEAD keyRoom2;

    static {
        try {
            keyRoom2 = new AEAD();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //args[0] holds the IP address of the zk server (it will always be on port 2181)
        //initiate the zookeeper watchers
        zkClient=new ZookeeperClient(args[0], args[1]);
        zkClient.registerLeaderChangeWatcher("/election", new LeaderChangeListener());
        //the value at args[1] is the IP and port that the server is listening on
        zkClient.electLeader(15_000);
        DatagramChannel channel = DatagramChannel.open().bind(new InetSocketAddress(args[1], 7086));
        for(;;){
            //listen for messages sent to this server, pass along the message to a request handler
            ByteBuffer data = ByteBuffer.allocate(1024);
            new Thread(new RequestHandler(channel.receive(data), data)).start();
        }
    }
}