package org.zk.watchers;

import org.I0Itec.zkclient.IZkDataListener;
import org.server.Main;
import org.server.packets.Factory;
import org.zk.dataClasses.PlayerCount;

import java.nio.ByteBuffer;

public class ClientCountChangeListener implements IZkDataListener {
    @Override
    public void handleDataChange(String path, Object o) throws Exception {
        //cast o to player count type
        long curTime = System.nanoTime();
        ByteBuffer pcBuf = new Factory().makePlayerCountPacket(((PlayerCount) o).getCount(), curTime);
        //just to make sure the position is where it needs to be
        pcBuf.position(0);

        //go through the list of clients in lobby, we only relay client count to the lobby screen so
        //for each in the lobby or a waiting for game to start state, send them the new lobby info now that
        //more people have joined
        for(String clientID:Main.zkClient.getWaitingClients()){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/lobby/waiting-clients/"+clientID, pcBuf)).start();
        }
        for(String clientID:Main.zkClient.getWaitingGameClients(0)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/0/waiting-players/"+clientID, pcBuf)).start();
        }

        for(String clientID:Main.zkClient.getWaitingGameClients(1)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/1/waiting-players/"+clientID, pcBuf)).start();
        }
        for(String clientID:Main.zkClient.getWaitingGameClients(2)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/2/waiting-players/"+clientID, pcBuf)).start();
        }
    }

    @Override
    public void handleDataDeleted(String path) throws Exception {
        //do nothing, we don't really care if data gets deleted (because it never will be). We are more interested in
        //data changing here
    }
}
