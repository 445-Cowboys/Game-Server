package org.server.packets;

import java.nio.ByteBuffer;

public class PlayerAction extends Packet{

    /*

    08 <Game room number> 0 <Action type> 0 < Player Number >

    */

    private final int gameRoom;
    private final int action;
    private final int playerNum;
    private final byte[] data;


    public PlayerAction(ByteBuffer buffer) {
        int totalLength = buffer.limit();
            this.data = new byte[totalLength];
            buffer.get(data, 0, data.length);
            buffer.rewind();
            this.gameRoom = buffer.getInt(1);
            this.action = buffer.getInt(6);
            this.playerNum = buffer.getInt(11);
    }

    @Override
    public int getOpcode(){
        return 8;
    }

    public int getGameRoom(){
        return gameRoom;
    }

    public int getAction(){
        return action;
    }

    public int getPlayerNum(){
        return playerNum;
    }

}
