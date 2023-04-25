package org.zkwatchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import java.util.concurrent.ThreadLocalRandom;

public class ZookeeperClient {
    private ZkClient zkClient;
    private int id = ThreadLocalRandom.current().nextInt();

    public ZookeeperClient(String zookeperServerLocation){
        System.out.println(id);
        zkClient = new ZkClient(zookeperServerLocation, 5000, 3000, new Serializer());
        if (!zkClient.exists("/election")) {
            zkClient.create("/election", "election node", CreateMode.PERSISTENT);
        }
    }

    //Check if there is a leader
    public boolean leaderExists(){return zkClient.exists("/election/master");}

    //get the leader node's IP
    public String getLeaderNode(){return zkClient.readData("/election/leader", true);}

    public void electLeader() {
        /*
         * makes sure that the election node first exists.
         * if it doesn't exist, create it
         */
        if(!zkClient.exists("/election"))
            zkClient.create("/election/leader", "election node", CreateMode.PERSISTENT);

        /*
         * Try making yourself the leader node
         * we use ephemeral nodes to represent the election sequence stuff you would see
         * in something like Paxos
         */
        try{
            zkClient.create("/election/leader", String.valueOf(id), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (ZkNodeExistsException e) {
            System.out.println("Leader already made");
            System.out.println(getLeaderNode());
        }
    }

    public void registerLeaderChangeWatcher(String path, IZkChildListener listener){
        zkClient.subscribeChildChanges(path, listener);
    }


}
