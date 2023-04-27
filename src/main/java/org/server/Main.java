package org.server;

import org.zk.watchers.LeaderChangeListener;
import org.zk.watchers.ZookeeperClient;

public class Main {
    public static ZookeeperClient zkClient;
    public static void main(String[] args) {
        //initiate the zookeeper watchers
        zkClient=new ZookeeperClient(args[0]);
        zkClient.registerLeaderChangeWatcher("/election", new LeaderChangeListener());
        //the value at args[1] is the IP and port that the server is listening on
        zkClient.electLeader();
        //a fun little forever loop just to show that it is monitoring new leader selection and all that cool stuff.
        for(;;){
            //just to show you that the leader node does, in fact, change!
//            System.out.println(zkClient.getLeaderNode());
        }
    }
}