package org.server.packets.packets;

import java.nio.ByteBuffer;

public class EnterRoomAck extends Packet{

    /*
    06 |0| <Successfully entered room>
    */

    private final byte[] data;
    private final boolean result;

    public EnterRoomAck(ByteBuffer buffer){

    int totalLength = buffer.limit();
        this.data = new byte[totalLength];
        buffer.get(data, 0, data.length);
        buffer.rewind();
            this.result = (buffer.get(2) & 0x01) != 0;
    }

    @Override
    public int getOpcode(){
        return 6;
    }

    public boolean getResult(){
        return result;
    }
}
