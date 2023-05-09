package org.server.packets;

import java.nio.ByteBuffer;

public class EnterRoom extends Packet {

    /*
    07 <Room they are entering> 0 <Port Number of Client> 0 <String of username>
    */

    private final byte[] data;
    private final int roomNum;

    private final int portNum;



    public EnterRoom(ByteBuffer buffer) {

    int totalLength = buffer.limit();
        this.data = new byte[totalLength];
        buffer.get(data, 0, data.length);
        buffer.rewind();
        this.roomNum = buffer.getInt(1);
        int offset = 6;
        buffer.position(offset);
        this.portNum = buffer.getInt(offset);

    }


    @Override
    public int getOpcode(){
        return 3;
    }

    public int getRoomNum(){
        return roomNum;
    }

    public int getPortNum() {
        return portNum;
    }


}
