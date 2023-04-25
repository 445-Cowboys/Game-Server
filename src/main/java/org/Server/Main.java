package org.Server;

import org.zkwatchers.LeaderChangeListener;
import org.zkwatchers.ZookeeperClient;

public class Main {
    public static ZookeeperClient zkClient;
    public static void main(String[] args) {
        //initiate the zookeeper watchers
        zkClient=new ZookeeperClient(args[0]);
        zkClient.registerLeaderChangeWatcher("/election", new LeaderChangeListener());
        zkClient.electLeader();
        //a fun little forever loop just to show that it is monitoring new leader selection and all that cool stuff.
        for(;;){}
    }
}