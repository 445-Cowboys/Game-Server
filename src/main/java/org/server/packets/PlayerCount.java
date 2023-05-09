package org.server.packets;

import java.nio.ByteBuffer;

public class PlayerCount extends Packet{
    private int playerCount;

    private long updateTime;

    public PlayerCount(ByteBuffer buf){
        playerCount = buf.getInt(2);
        updateTime = buf.getLong(6);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public long getUpdateTime() {return updateTime;}

    @Override
    public int getOpcode() {
        return 10;
    }
}
