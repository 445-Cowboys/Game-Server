package org.server.packets;

import java.nio.ByteBuffer;

public class PlayerAction extends Packet{

    /*

    08 <Game room number> 0 <Action type> 0 < Player Number >

    */

    private final int gameRoom;
    private final int action;
    private final int playerNum;


    public PlayerAction(ByteBuffer buffer) {
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
