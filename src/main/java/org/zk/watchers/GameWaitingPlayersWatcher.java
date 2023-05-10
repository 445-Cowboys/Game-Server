package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

public class GameWaitingPlayersWatcher implements IZkChildListener {
    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {

    }
}
