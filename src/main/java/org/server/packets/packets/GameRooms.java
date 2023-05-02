package org.server.packets.packets;

import java.nio.ByteBuffer;

public class GameRooms extends Packet {

    /*
    6 <R1 Number of players>|<R1 IsRoomFull>|<R1 Status>|0|
      <R2 Number of players>|<R2 IsRoomFull>|<R2 Status>|0|
      <R3 Number of players>|<R3 IsRoomFull>|<R3 Status>|0|
      <Server 1 Status> <Server 2 Status> <Server 3 Status>  <
     */

    private final byte[] data;
    private final int[] numPlayers;
    private final boolean[] roomFull;
    private final int[] roomStatus;
    private final int[] serverStatus;


    public GameRooms(ByteBuffer buffer){

    int totalLength = buffer.limit();
        this.data = new byte[totalLength];
        buffer.get(data, 0, data.length);
        buffer.rewind();

        this.numPlayers = new int[3];
        this.roomFull = new boolean[3];
        this.roomStatus = new int[3];
        this.serverStatus = new int[3];
        int offset = 1;

        for (int i = 0; i < 3; i++) {
            this.numPlayers[i] = buffer.getInt(offset);
            offset += 4;
            this.roomFull[i] = (buffer.get(offset) & 0x01) != 0;
            offset += 1;
            this.roomStatus[i] = buffer.getInt(offset);
            offset += 5;
        }
        for (int i = 0; i < 3; i++) {
            this.serverStatus[i] = buffer.getInt(offset);
            if ( i < 2 ) offset += 4;
        }
    }

    @Override
    public int getOpcode(){
        return 5;
    }

    public int getNumPlayers(int room) {
        return numPlayers[room];
    }

    public boolean getRoomFull(int room) {
        return roomFull[room];
    }

    public int getRoomStatus(int room) {
        return roomStatus[room];
    }

    public int getServerStatus(int server) {
        return serverStatus[server];
    }


}
