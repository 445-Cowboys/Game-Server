package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

public class GameRoomsInfo extends ZookeeperData{
    private List<GameRoom> gameRooms;

    public GameRoomsInfo(){
        gameRooms = Arrays.asList(new GameRoom(), new GameRoom(), new GameRoom());
    }

    public GameRoomsInfo(GameRoom gR1, GameRoom gR2, GameRoom gR3){
        gameRooms = Arrays.asList(gR1,gR2,gR3);
    }

    public GameRoomsInfo(List<GameRoom> gameRooms){
        this.gameRooms=gameRooms;
    }


    /**
     * Get a specified game room
     * @param index the number of the room
     * @return the specified game room info
     */
    public GameRoom getGameRoom(int index){
        return gameRooms.get(index);
    }

    /**
     * Attempts to add a player to the given game room
     * @param index index of the given game room
     * @return true if add was successful, false otherwise
     */
    public boolean addPlayer(int index){
        return gameRooms.get(index).addPlayer();
    }

    /**
     * Attempts to remove a player from a given game room
     * @param index index of the given game room
     * @return true if remove was successful, false otherwise
     */
    public boolean removePlayer(int index){
        return gameRooms.get(index).removePlayer();
    }


    /**
     * Serializes the object to put it in a Z-Node
     * @return bytes array of the serialized game rooms info object
     */
    @Override
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(gameRooms);
        }catch (IOException e){
            e.printStackTrace();
        }
        return Bytes.concat(new byte[]{Integer.valueOf(4).byteValue(), Integer.valueOf(0).byteValue()}, bos.toByteArray());
    }
}
