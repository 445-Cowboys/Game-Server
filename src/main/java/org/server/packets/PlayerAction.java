package org.server.packets;

import java.nio.ByteBuffer;

public class PlayerAction extends Packet{

    /*

    08 <Game room number> 0 <Action type> 0 <player you heal if action is type 3 and your attribute is a healer>

    */

    private final int gameRoom;

    private final int action;

    private final int heal;
    private final byte[] data;

    public PlayerAction(ByteBuffer buffer) {
        int totalLength = buffer.limit();
            this.data = new byte[totalLength];
            buffer.get(data, 0, data.length);
            buffer.rewind();
            this.gameRoom = buffer.getInt(1);
            this.action = buffer.getInt(6);

            //If not a healer this should always be 0,
            //if using healer ability the player number will be listed.
            this.heal = buffer.getInt(11);
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

    public int getHeal(){
        return heal;
    }

}
