package org.zk.watchers;

import org.I0Itec.zkclient.IZkDataListener;

public class LobbyStatsWatcher implements IZkDataListener {
    @Override
    public void handleDataChange(String s, Object o) throws Exception {
        //if there is a change in the lobby info, the waiting clients
        //and waiting game players need to be alerted.

        //add a timestamp for the client.
        long curTime = System.nanoTime();

        //go through each player that would have the lobby screen open at the moment.

    }

    @Override
    public void handleDataDeleted(String s) throws Exception {
        //It will never be deleted so don't even worry about it.
    }
}
