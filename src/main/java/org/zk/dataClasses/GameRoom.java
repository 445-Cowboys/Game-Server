package org.zk.dataClasses;

import java.io.Serializable;

public class GameRoom implements Serializable {
    private Integer size=0;
    //1=game is in session, 2=game has finished, 3=game room is open for a new session
    private Integer state=0;
    private Boolean roomIsFull=false;

    public GameRoom(){}

    public GameRoom(int size, int state, boolean roomIsFull){
        this.size=size;
        this.state=state;
        this.roomIsFull=roomIsFull;
    }

    /**
     * Increments the number of people in the room
     * @return true if player added, false if not, meaning that the room was full
     */
    public boolean addPlayer(){
        if(roomIsFull) {
            return false;
        }
        size++;
        if(size==3)
            roomIsFull=true;
        return true;
    }

    /**
     * Decrements player count in a room
     * @return true if player removed, false if player not removed, meaning it would drop the size to a negative number
     */
    public boolean removePlayer(){
        if(size==0)
            return false;
        size--;
        if(size<3)
            roomIsFull=false;
        return true;
    }

    /**
     * Changes the game room state
     * @param state the new game room state
     */
    public void changeState(int state){
        this.state=state;
    }

    public void resetSize(){size = 0;}

    public Integer getSize() {
        return size;
    }

    public Integer getState() {
        return state;
    }

    public Boolean getRoomIsFull() {
        return roomIsFull;
    }
}
