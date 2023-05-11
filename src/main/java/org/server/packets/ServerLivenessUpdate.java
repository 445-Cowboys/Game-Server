package org.server.packets;

import java.nio.ByteBuffer;

public class ServerLivenessUpdate {
    private final int[] serverStates=new int[3];
    private final long timeStamp;

    public ServerLivenessUpdate(ByteBuffer buf){
        serverStates[0] = buf.getInt(2);
        serverStates[1] = buf.getInt(6);
        serverStates[2] = buf.getInt(10);
        timeStamp = buf.getLong(14);
    }

    public int[] getServerStates(){return serverStates;}

}
