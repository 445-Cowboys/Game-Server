package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.zk.dataClasses.GameRoomsInfo;
import org.zk.dataClasses.GameState;
import org.zk.dataClasses.PlayerCount;
import org.zk.dataClasses.ServerData;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZookeeperClient {
    private final ZkClient zkClient;
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    private final String id;

    public ZookeeperClient(String zookeperServerLocation, String id) {
        this.id = id;
        zkClient = new ZkClient(zookeperServerLocation, 5000, 10000, new Serializer());

        if(!zkClient.exists("/election")){
            zkClient.createPersistent("/election");
        }

        //start up by making sure all the paths needed are created
        if(!zkClient.exists("/game-states")){
            zkClient.createPersistent("/game-states");
        }

        //under lobby, will be lobby/lobby-stats
        //and lobby/clients-in-lobby
        //clients-in-lobby will decrease as games start up
        if(!zkClient.exists("/lobby")){
            zkClient.createPersistent("/lobby");
        }

        //holds the encryption keys we will use for communication to our client
        if(!zkClient.exists("/client-keys")){
            zkClient.createPersistent("/client-keys");
        }

        //nodes that hold the IPs of the live servers.
        if(!zkClient.exists("/live-servers")){
            zkClient.createPersistent("/live-servers");
        }

        //get the number of children that '/live-servers' has. if it is zero, then all the servers have previously died
        //and we should reset all the values that we have.
        //we make the indicator of the server ephermal since we want it to go away when the server crashes
        Outer: if(zkClient.getChildren("/live-servers").size() != 0){
            if(zkClient.exists("/live-server/"+id)) break Outer;
            zkClient.createEphemeral("/live-servers/"+ id);
            return;
        }

        //if we get here then the number of live servers was zero. Reset everything and then add ourselves to the
        //live servers znode

        //first get the lock for game lobby info, reset it all to zeroes
        if(!zkClient.exists("/lobby/stats")){
            zkClient.createPersistent("/lobby/stats", new GameRoomsInfo());
        }else{
            int idVal = getWriteLock("/lobby/stats");
            zkClient.writeData("/lobby/stats", new GameRoomsInfo());
            releaseWriteLock("/lobby/stats", idVal);
        }

        if(!zkClient.exists("/lobby/waiting-clients")){
            zkClient.createPersistent("/lobby/waiting-clients");
        }else{
            int idVal = getWriteLock("/lobby/waiting-clients");
            for(String client:zkClient.getChildren("/lobby/waiting-clients"))
                if(!client.contains("write-lock"))
                    zkClient.delete("/lobby/waiting-clients/"+client);
            releaseWriteLock("/lobby/waiting-clients", idVal);
        }

        //on game start, we will initialize these values
        //everytime a player makes their move in the game, we set the player-has-gone-value to true
        //if that value is not "true" within let's say 60 seconds, we assume the client has left,
        //and we remove them from the list of active players in the game room
        //we'll monitor the "player-has-gone" value by having it check the node's value
        //and sleep for a second if the value continues to be false
        //the leader will be the one designated to do this job so that there is always only one server monitoring the
        // players who still need to go.
        //the values will initially be null, meaning a game has not started
        if(!zkClient.exists("/game-rooms")){
            zkClient.createPersistent("/game-rooms");
        }

        if(!zkClient.exists("/player-count")){
            zkClient.createPersistent("/player-count", new PlayerCount());
        }else{
            zkClient.writeData("/player-count", new PlayerCount());
        }

        if(!zkClient.exists("/game-rooms/0")){
            zkClient.createPersistent("/game-rooms/0");
            zkClient.createPersistent("/game-rooms/0/waiting-players");
            zkClient.createPersistent("/game-rooms/0/live-players");
        }else{
            for(String client:zkClient.getChildren("/game-rooms/0/waiting-players"))
                zkClient.delete("/game-rooms/0/waiting-players/"+client);

            for(String client:zkClient.getChildren("/game-rooms/0/live-players"))
                zkClient.delete("/game-rooms/0/live-players/"+client);
        }

        if(!zkClient.exists("/game-rooms/1")){
            zkClient.createPersistent("/game-rooms/1");
            zkClient.createPersistent("/game-rooms/1/waiting-players");
            zkClient.createPersistent("/game-rooms/1/live-players");
        }else{
            for(String client:zkClient.getChildren("/game-rooms/1/waiting-players"))
                zkClient.delete("/game-rooms/1/waiting-players/"+client);

            for(String client:zkClient.getChildren("/game-rooms/1/live-players"))
                zkClient.delete("/game-rooms/1/live-players/"+client);
        }

        if(!zkClient.exists("/game-rooms/2")){
            zkClient.createPersistent("/game-rooms/2");
            zkClient.createPersistent("/game-rooms/2/waiting-players");
            zkClient.createPersistent("/game-rooms/2/live-players");
        }else{
            for(String client:zkClient.getChildren("/game-rooms/2/waiting-players"))
                zkClient.delete("/game-rooms/2/waiting-players/"+client);

            for(String client:zkClient.getChildren("/game-rooms/2/live-players"))
                zkClient.delete("/game-rooms/2/live-players/"+client);
        }

        zkClient.createEphemeral("/live-servers/"+id);
    }

    public int checkServerStatus(String id){
        if(zkClient.exists("/live-servers/"+id)){
            if(getLeaderNode().getAddress().equals(id)){
                return 1;
            }
            return 2;
        }
        return 0;
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
    public ServerData getLeaderNode(){return zkClient.readData("/election/leader", true);}

    /**
     * Return the info about how many people are in each game room and if a session is started, over, or open for new players
     * @return GameRoomsInfo data class
     */
    public GameRoomsInfo getGameRoomsInfo(){return zkClient.readData("/lobby/stats", true);}

    /**
     * Get the clients currently waiting in the lobby
     * @return The list of clients currently in the lobby
     */
    public List<String> getWaitingClients(){
        int idVal = getReadLock("/lobby/waiting-clients");
        List<String> clients = zkClient.getChildren("/lobby/waiting-clients");
       releaseReadLock("/lobby/waiting-clients", idVal);
       return clients;
    }

    /**
     * Get the clients who are currently in a game room
     * @param gameRoomNum the game room number
     * @return the list of clients currently in a game
     */
    public List<String> getGameClients(int gameRoomNum){
        List<String> clients = zkClient.getChildren("/game-rooms/"+gameRoomNum+"/live-players");
        return clients;
    }

    /**
     * Get the clients who are currently in a game room but waiting for it to start
     * @param gameRoomNum the game room number
     * @return the list of clients currently in a game
     */
    public List<String> getWaitingGameClients(int gameRoomNum){
        List<String> clients = zkClient.getChildren("/game-rooms/"+gameRoomNum+"/waiting-players");
        return clients;
    }

    public GameState getGameState(int gameRoomNum){
        int idVal = getReadLock("/game-rooms/"+gameRoomNum);
        GameState gs = zkClient.readData("/game-rooms/"+gameRoomNum);
        releaseReadLock("/game-rooms/"+gameRoomNum, idVal);
        return gs;
    }


    /**
     * Add a new client to the list of clients waiting in the lobby
     * @param playerAddress IP address and port of added player
     */
    public void addPlayerToLobby(String playerAddress){
        //add the new address to the list of waiting clients
        if(zkClient.exists("/lobby/waiting-clients"+playerAddress)) return;
        zkClient.createPersistent("/lobby/waiting-clients"+playerAddress);
        //increment the player count
        int idVal = getWriteLock("/player-count");
        zkClient.writeData("/player-count", ((PlayerCount) zkClient.readData("/player-count")).increment());
        releaseWriteLock("/player-count", idVal);
    }

    public void decrementPlayerCount(){
        int idVal = getWriteLock("/player-count");
        zkClient.writeData("/player-count", ((PlayerCount) zkClient.readData("/player-count")).decrement());
        releaseWriteLock("/player-count", idVal);
    }

    public int getPlayerCount(){
        int idVal = getReadLock("/player-count");
        PlayerCount pc = zkClient.readData("/player-count");
        releaseReadLock("/player-count", idVal);
        return pc.getCount();
    }



    /**
     * Get the write lock on a specific znode; once the lock is gotten, the current server is free to write to the specific location
     * @param path the path of the znode we want to modify
     */
    public int getWriteLock(String path){
        int id = ThreadLocalRandom.current().nextInt();
        //create the read-lock path if one does not exist already
        if(!zkClient.exists(path+"/read-lock")){
            zkClient.createPersistent(path+"/read-lock");
        }
        //create the write-lock path if one does not exist already
        if(!zkClient.exists(path+"/write-lock")){
            zkClient.createPersistent(path+"/write-lock");
        }

        //create our write lock. this will stop any further readers from getting made while we wait
        //for the existing readers to finish
        zkClient.createEphemeralSequential(path+"/write-lock/lock-", new ServerData(String.valueOf(id)));
        //get the current number of readers
        //and all the writers that are currently waiting to acquire the lock
        //when there are no readers left, and we are next in line for the lock, we return out of the function
        int numOfReaders = zkClient.getChildren(path+"/read-lock").size();
        List<String> writers = zkClient.getChildren(path+"/write-lock");
        //sort the requested writers
        writers.sort(String::compareTo);
        ServerData curLockHolder = zkClient.readData(path+"/write-lock/"+writers.get(0));
        //loop until we are next in line to write and we have no other readers
        //we add the null check in case the current lock holder deletes and we are dealing with old data
        while(curLockHolder==null || !curLockHolder.getAddress().equals(String.valueOf(id)) || numOfReaders!=0){
            numOfReaders = zkClient.getChildren(path+"/read-lock").size();
            writers = zkClient.getChildren(path+"/write-lock");
            writers.sort(String::compareTo);
            curLockHolder = zkClient.readData(path+"/write-lock/"+writers.get(0), true);
        }
        return id;
    }

    /**
     * Release the write lock
     * @param path the data value location we want to release the lock of
     */
    public void releaseWriteLock(String path, int id){
        for(String child:zkClient.getChildren(path+"/write-lock")){
            //find the writer we want to get rid of, delete it, and then return.
            if(((ServerData)zkClient.readData(path+"/write-lock/"+child)).getAddress().equals(String.valueOf(id))){
                zkClient.delete(path+"/write-lock/"+child);
                System.out.println("write lock released");
                return;
            }
        }
    }


    /**
     * Get the read lock for a specific Znode. multiple clients can hold a write lock at the same time
     * No writes will happen until there are no more readers
     * @param path the location of the data we want to read
     */
    public int getReadLock(String path){
        //create the initial lock node if one does not exist
        int id = ThreadLocalRandom.current().nextInt();
        if(!zkClient.exists(path+"/read-lock")){
            zkClient.createPersistent(path+"/read-lock");
        }
        //if the write-lock node doesn't even exist, we know no writers currently exist so just make it for
        //future use and grab the read lock
        if(!zkClient.exists(path+"/write-lock")){
            zkClient.createPersistent(path+"/write-lock");
            zkClient.createEphemeralSequential(path+"/read-lock/lock-", new ServerData(String.valueOf(id)));
            return id;
        }
        List<String> writers = zkClient.getChildren(path+"/write-lock");
        //keep looping here until there are no more writers, once all the writers are gone then we can exit,
        //knowing for sure that only readers exist for the given object
        while(writers.size()!=0){
            System.out.println(writers.size());
            System.out.println(writers.get(0));
            writers = zkClient.getChildren(path+"/write-lock");
        }

        //if we get here, we know there are no more writers queued up, so we can grab the read lock
        //create our ephemeral sequential node. we won't actually use any of the sequential propertiy things, but just use it
        //so that multiple read locks can get allocated
        System.out.println("Now reading "+path);
        zkClient.createEphemeralSequential(path+"/read-lock/lock-", new ServerData(String.valueOf(id)));
        return id;
    }


    /**
     * Releases the read lock of the specified data
     * @param path the location of the data we want to release the read lock of
     */
    public void releaseReadLock(String path, int id){
        for(String child:zkClient.getChildren(path+"/read-lock")){
            //find the reader we want to get rid of, delete it, and then return.
            if(((ServerData)zkClient.readData(path+"/read-lock/"+child)).getAddress().equals(String.valueOf(id))){
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
    public void electLeader(long delay) {
        /*
         * Try making yourself the leader node
         * we use ephemeral nodes to represent the election sequence stuff you would see
         * in something like Paxos
         */
        try{
            zkClient.createEphemeral("/election/leader", new ServerData(id));
            isLeader.set(true);
            //spin up a thread that will send heartbeats to the clients every thirty seconds
            new Thread(new ClientWatcher(delay)).start();
            zkClient.subscribeDataChanges("/game-rooms/1", new GameStateChangeListener());
            zkClient.subscribeDataChanges("/game-rooms/2", new GameStateChangeListener());
            zkClient.subscribeDataChanges("/game-rooms/3", new GameStateChangeListener());
            zkClient.subscribeDataChanges("/player-count", new ClientCountChangeListener());
        } catch (ZkNodeExistsException e) {
            System.out.println("Leader already made: "+getLeaderNode().getAddress());
        }
    }

    /**
     * Returns a boolean saying if the server is currently the leader or not
     * @return True if leader, false otherwise
     */
    public boolean getIsLeader() {
        return isLeader.get();
    }

    /**
     * Register a listener for the server that will run everytime a change is detected in the znode
     * @param path path of node to watch
     * @param listener listener class
     */
    public void registerLeaderChangeWatcher(String path, IZkChildListener listener){
        zkClient.subscribeChildChanges(path, listener);
    }


    /**
     * Delete node at given path
     * @param path path to delete node
     */
    public void deleteNode(String path) {
        zkClient.delete(path);
    }
}
