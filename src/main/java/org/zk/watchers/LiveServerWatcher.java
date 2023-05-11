package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.server.Main;
import org.server.packets.Factory;

import java.nio.ByteBuffer;
import java.util.List;

public class LiveServerWatcher implements IZkChildListener {

    @Override
    public void handleChildChange(String s, List<String> list) throws Exception {
        int[] serverStatus = new int[3];
        serverStatus[0] = Main.zkClient.checkServerStatus("rho.cs.oswego.edu");
        serverStatus[1] = Main.zkClient.checkServerStatus("moxie.cs.oswego.edu");
        serverStatus[2] = Main.zkClient.checkServerStatus("altair.cs.oswego.edu");
        long curTime = System.nanoTime();
        ByteBuffer buf = new Factory().makeServerStatesPacket(serverStatus, curTime);
        //now that we have the buffer, go through each client inside the waiting clients and send it to them.
        //go through each player that would have the lobby screen open at the moment.
        for(String clientID:Main.zkClient.getWaitingClients()){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/lobby/waiting-clients/"+clientID, buf)).start();
        }
        for(String clientID:Main.zkClient.getWaitingGameClients(0)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")||Main.zkClient.pathExists("/game-rooms/0/live-players/"+clientID)) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/0/waiting-players/"+clientID, buf)).start();
        }

        for(String clientID:Main.zkClient.getWaitingGameClients(1)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")||Main.zkClient.pathExists("/game-rooms/1/live-players/"+clientID)) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/1/waiting-players/"+clientID, buf)).start();
        }
        for(String clientID:Main.zkClient.getWaitingGameClients(2)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")||Main.zkClient.pathExists("/game-rooms/2/live-players/"+clientID)) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/2/waiting-players/"+clientID, buf)).start();
        }
    }
}
