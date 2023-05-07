package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

public class GamePlayerListWatcher implements IZkChildListener {


    //when this hits three players tell the server to start up
    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        if(list.size() == 3){
            //we have enough people, get ready to send the game start packet to all the players.
        }
    }
}
