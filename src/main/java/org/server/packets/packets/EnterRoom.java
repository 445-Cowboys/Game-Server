package org.server.packets.packets;

import java.nio.ByteBuffer;

public class EnterRoom extends Packet {

    /*
    07 <Room they are entering> 0 <String of username>
    */

    private final byte[] data;
    private final int roomNum;
    private final String userName;


    public EnterRoom(ByteBuffer buffer) {

    int totalLength = buffer.limit();
        this.data = new byte[totalLength];
        buffer.get(data, 0, data.length);
        buffer.rewind();
        this.roomNum = buffer.getInt(1);
        int offset = 6;

        byte[] userNameBytes = new byte[totalLength - offset];
        buffer.position(offset);
        buffer.get(userNameBytes, 0, userNameBytes.length);
        userName = new String(userNameBytes);

    }


    @Override
    public int getOpcode(){
        return 9;
    }

    public int getRoomNum(){
        return roomNum;
    }

    public String getuserName(){
        return userName;
    }

}
