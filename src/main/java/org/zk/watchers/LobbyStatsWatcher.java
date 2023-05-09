package org.zk.watchers;

import org.I0Itec.zkclient.IZkDataListener;
import org.server.Main;
import org.server.packets.Factory;
import org.zk.dataClasses.GameRoomsInfo;

import java.nio.ByteBuffer;

public class LobbyStatsWatcher implements IZkDataListener {
    @Override
    public void handleDataChange(String s, Object o) throws Exception {
        //if there is a change in the lobby info, the waiting clients
        //and waiting game players need to be alerted.

        //add a timestamp for the client.
        long curTime = System.nanoTime();
        GameRoomsInfo gfInfo = (GameRoomsInfo) o;
        int lockId = Main.zkClient.getReadLock("/lobby/stats");
        ByteBuffer buffer = new Factory().makeGameRoomsUpdate(new int[]{gfInfo.getGameRoom(0).getSize(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new boolean[]{gfInfo.getGameRoom(0).getRoomIsFull(), gfInfo.getGameRoom(1).getRoomIsFull(), gfInfo.getGameRoom(2).getRoomIsFull()},new int[]{gfInfo.getGameRoom(0).getState(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new int[]{Main.zkClient.checkServerStatus("rho.cs.oswego.edu"),Main.zkClient.checkServerStatus("moxie.cs.oswego.edu"),Main.zkClient.checkServerStatus("altair.cs.oswego.edu")}, curTime);
        Main.zkClient.releaseReadLock("/lobby/stats", lockId);
        //now that we have the buffer, go through each client inside the waiting clients and send it to them.
        //go through each player that would have the lobby screen open at the moment.
        for(String clientID:Main.zkClient.getWaitingClients()){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/lobby/waiting-clients/"+clientID, buffer)).start();
        }
        for(String clientID:Main.zkClient.getWaitingGameClients(0)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/0/waiting-players/"+clientID, buffer)).start();
        }

        for(String clientID:Main.zkClient.getWaitingGameClients(1)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/1/waiting-players/"+clientID, buffer)).start();
        }
        for(String clientID:Main.zkClient.getWaitingGameClients(2)){
            if(clientID.contains("write-lock")||clientID.contains("read-lock")) continue;
            new Thread(new PacketSender(clientID, 10, "/game-rooms/2/waiting-players/"+clientID, buffer)).start();
        }

    }

    @Override
    public void handleDataDeleted(String s) throws Exception {
        //It will never be deleted so don't even worry about it.
    }
}
