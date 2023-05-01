package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.zk.dataClasses.GameRoomsInfo;
import org.zk.dataClasses.GameState;
import org.zk.dataClasses.Leader;
import org.zk.dataClasses.ZookeeperData;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZookeeperClient {
    private ZkClient zkClient;

    private AtomicBoolean isLeader = new AtomicBoolean(false);
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
     * Get the write lock on a specific znode, once the lock is gotten, the current server is free to write to the specific location
     * @param path the path of the znode we want to modify
     */
    public void getWriteLock(String path){
        //create the read-lock path if one does not exist already
        if(!zkClient.exists(path+"/read-lock")){
            zkClient.createPersistent(path+"/read-lock");
        }
        //create the write-lock path if one does not exist already
        if(!zkClient.exists(path+"/write-lock")){
            zkClient.createPersistent(path+"write-lock");
        }

        //create our write lock. this will stop any further readers from getting made while we wait
        //for the existing readers to finish
        zkClient.createEphemeralSequential(path+"/write-lock/lock-", new Leader(String.valueOf(id)));
        //get the current number of readers
        //and all the writers that are currently waiting to acquire the lock
        //when there are no readers left, and we are next in line for the lock, we return out of the function
        int numOfReaders = zkClient.getChildren(path+"/read-lock").size();
        System.out.println(zkClient.getChildren(path+"/read-lock"));
        List<String> writers = zkClient.getChildren(path+"/write-lock");
        //sort the requested writers
        writers.sort(String::compareTo);
        System.out.println(writers);
        Leader curLockHolder = zkClient.readData(path+"/write-lock/"+writers.get(0));
        //loop until we are next in line to write and we have no other readers
        while(!curLockHolder.getAddress().equals(String.valueOf(id)) || numOfReaders!=0){
            System.out.println("can't write yet");
            numOfReaders = zkClient.getChildren(path+"/read-lock").size();
            writers = zkClient.getChildren(path+"/write-lock");
            writers.sort(String::compareTo);
            curLockHolder = zkClient.readData(path+"/write-lock/"+writers.get(0));
        }
        System.out.println("writing!");
    }

    /**
     * Release the write lock
     * @param path the data value location we want to release the lock of
     */
    public void releaseWriteLock(String path){
        for(String child:zkClient.getChildren(path+"/write-lock")){
            //find the reader we want to get rid of, delete it, and then return.
            if(((Leader)zkClient.readData(path+"/write-lock/"+child)).getAddress().equals(String.valueOf(id))){
                zkClient.delete(path+"/write-lock/"+child);
                return;
            }
        }
    }


    /**
     * Get the read lock for a specific Znode. multiple clients can hold a write lock at the same time
     * No writes will happen until there are no more readers
     * @param path the location of the data we want to read
     */
    public void getReadLock(String path){
        //create the initial lock node if one does not exist
        if(!zkClient.exists(path+"/read-lock")){
            zkClient.createPersistent(path+"/read-lock");
        }
        //if the write-lock node doesn't even exist, we know no writers currently exist so just make it for
        //future use and grab the read lock
        if(!zkClient.exists(path+"/write-lock")){
            zkClient.createPersistent(path+"/write-lock");
            zkClient.createEphemeralSequential(path+"read-lock/lock-", new Leader(String.valueOf(id)));
            return;
        }
        List<String> writers = zkClient.getChildren(path+"/write-lock");
        //keep looping here until there are no more writers, once all the writers are gone then we can exit,
        //knowing for sure that only readers exist for the given object
        while(writers.size()!=0){
            writers = zkClient.getChildren(path+"/write-lock");
        }

        //if we get here, we know there are no more writers queued up, so we can grab the read lock
        //create our ephemeral sequential node. we won't actually use any of the sequential propertiy things, but just use it
        //so that multiple read locks can get allocated
        zkClient.createEphemeralSequential(path+"/read-lock/lock-", new Leader(String.valueOf(id)));
    }


    /**
     * Releases the read lock of the specified data
     * @param path the location of the data we want to release the read lock of
     */
    public void releaseReadLock(String path){
        for(String child:zkClient.getChildren(path+"/read-lock")){
            //find the reader we want to get rid of, delete it, and then return.
            if(((Leader)zkClient.readData(path+"/read-lock/"+child)).getAddress().equals(String.valueOf(id))){
                zkClient.delete(path+"/read-lock/"+child);
                System.out.println("Read lock released!");
                return;
            }
        }
    }

    /**
     * Elects a new leader by trying to put itself in the leader node before the other live servers,
     * the server that gets to the node first wins and becomes the leader.
     * Upon winning the election, the new leader begins listening for requests from the other servers
     * int the cluster, even the one that just went down since it is possible it will come back up and
     * become a follower.
     */
    public void electLeader() {
        /*
         * Try making yourself the leader node
         * we use ephemeral nodes to represent the election sequence stuff you would see
         * in something like Paxos
         */
        try{
            zkClient.createEphemeral("/election/leader", new Leader(String.valueOf(id)));
            isLeader.set(true);
            //if we get here that means we became the leader, spin up some follower listener threads now that will
            //retransmit any packets that come to them back to the receiver
        } catch (ZkNodeExistsException e) {
            System.out.println("Leader already made: "+getLeaderNode().getAddress());
        }
    }

    public boolean getIsLeader() {
        return isLeader.get();
    }

    public void registerLeaderChangeWatcher(String path, IZkChildListener listener){
        zkClient.subscribeChildChanges(path, listener);
    }


}
