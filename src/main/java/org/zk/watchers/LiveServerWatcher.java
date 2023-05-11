package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.server.Main;

import java.util.List;

public class LiveServerWatcher implements IZkChildListener {

    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        int[] serverStatus = new int[3];
        serverStatus[0] = Main.zkClient.checkServerStatus("rho.cs.oswego.edu");
        serverStatus[1] = Main.zkClient.checkServerStatus("moxie.cs.oswego.edu");
        serverStatus[2] = Main.zkClient.checkServerStatus("altair.cs.oswego.edu");
        long curTime = System.nanoTime();
    }
}
