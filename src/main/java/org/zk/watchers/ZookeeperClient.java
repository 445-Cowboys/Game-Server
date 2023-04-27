package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.zk.dataClasses.GameRoomsInfo;
import org.zk.dataClasses.GameState;
import org.zk.dataClasses.Leader;

import java.util.concurrent.ThreadLocalRandom;

public class ZookeeperClient {
    private ZkClient zkClient;
    private int id = ThreadLocalRandom.current().nextInt();

    public ZookeeperClient(String zookeperServerLocation){
        System.out.println(id);
        zkClient = new ZkClient(zookeperServerLocation, 5000, 3000, new Serializer());
    }

    /**
     *
     * @return True if there is a leader, false otherwise
     */
    public boolean leaderExists(){return zkClient.exists("/election/leader");}

    /**
     *
     * @return The leader node's IP address and port number to forward data to
     */
    public Leader getLeaderNode(){return zkClient.readData("/election/leader", true);}

    /**
     * Return the info about how many people are in each game room and if a session is started, over, or open for new players
     * @return GameRoomsInfo data class
     */
    public GameRoomsInfo getGameRoomsInfo(){return zkClient.readData("/game-room-stats", true);}

    /**
     * Return the game state of a given room
     * @param gameRoom the number of the room we want to get the game state for
     * @return The game state of the given game room
     */
    public GameState getGameState(int gameRoom){return zkClient.readData("/game-state/"+gameRoom,true);}

    /**
     * Elects a new leader by trying to put itself in the leader node before the other live servers,
     * the server that gets to the node first wins and becomes the leader.
     * Upon winning the election, the new leader begins listening for requests from the other servers
     * int the cluster, even the one that just went down since it is possible it will come back up and
     * become a follower.
     */

    public void electLeader() {
        /*
         * makes sure that the election node first exists.
         * if it doesn't exist, create it
         */
        if(!zkClient.exists("/election"))
            zkClient.create("/election/leader", new Leader("election node"), CreateMode.EPHEMERAL);

        /*
         * Try making yourself the leader node
         * we use ephemeral nodes to represent the election sequence stuff you would see
         * in something like Paxos
         */
        try{
            zkClient.create("/election/leader", new Leader(String.valueOf(id)), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (ZkNodeExistsException e) {
            System.out.println("Leader already made: "+getLeaderNode().getAddress());
        }
    }

    public void registerLeaderChangeWatcher(String path, IZkChildListener listener){
        zkClient.subscribeChildChanges(path, listener);
    }


}
