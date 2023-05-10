package org.zk.watchers;

import org.I0Itec.zkclient.IZkDataListener;
import org.server.Main;
import org.zk.dataClasses.GameState;

public class GameStateChangeListener implements IZkDataListener {
    @Override
    public void handleDataChange(String path, Object o) throws Exception {
        //cast the object to a GameState type
        GameState gs = ((GameState) o);
        //get the list of clients who are currently in the game, relay the game state change to them
        //set a read lock on the path so that no one tries to write as we do this, if someone is writing to it as
        //we attempt to read then it is fine, the frame number will be lower than the previous one and the client
        String idVal = Main.zkClient.getReadLock(path);
        //will just discard it when we send it, but will still return an ack.
        for(String player: Main.zkClient.getGameClients(Integer.parseInt(path.split("/")[1]))){
            //player will hold the ip and listening port in the format "<IP>/<Port>"
            String ip = player.split("/")[0];
            int port = Integer.parseInt(player.split("/")[1]);
        }
        //after the state gets sent to all the children, release the read lock
        Main.zkClient.releaseReadLock(idVal);
    }

    @Override
    public void handleDataDeleted(String path) throws Exception {
        //Don't care about it getting deleted
        //(we never do that, we only change the values)
    }
}
