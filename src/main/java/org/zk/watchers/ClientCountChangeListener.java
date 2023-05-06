package org.zk.watchers;

import org.I0Itec.zkclient.IZkDataListener;
import org.zk.dataClasses.PlayerCount;

public class ClientCountChangeListener implements IZkDataListener {
    @Override
    public void handleDataChange(String path, Object o) throws Exception {
        //cast o to player count type
        PlayerCount pc = ((PlayerCount) o);
        //go through the list of clients in lobby, we only relay client count to the lobby screen so
        //for each in the lobby or a waiting for game to start state, send them the new lobby info now that
        //more people have joined
        //we could probably just make a new packet called "Player count"
        //to send to the waiting clients so that people entering and exiting rooms don't get too held up

    }

    @Override
    public void handleDataDeleted(String path) throws Exception {
        //do nothing we don't really care if data gets deleted (because it never will be). We are more interested in
        //data changing here
    }
}
