package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class GameState extends ZookeeperData implements Serializable {

    private final Player[] players;
    private int numPlayers;
    private int currentPlayer;
    private String actionMessage;

    private int frameNum;

    public GameState() {
        //first frame number will always be zero
        this.frameNum = 0;
        this.players = new Player[4];
        this.numPlayers = 0;
        this.currentPlayer = 0;

        this.players[3] = new Player(Character.DOUG_LEA);
    }

    public void addPlayer() {
        players[numPlayers] = new Player(Character.getPlayer(numPlayers));
        numPlayers++;
    }
    public Player[] getPlayers(){return players;}

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public void bossTurn() {
        Player boss = players[3];

        //get rid of the defend stuff for now...
//        if ((int) Math.floor(Math.random() * 4) == 3) {
//            defend();
//            return;
//        }

        if (boss.getAmmo() == 0) {
            reload();
            return;
        }
        int player_index = (int) Math.floor(Math.random() * 3);
        while(players[player_index].getHealth() <= 0)
            player_index = (int) Math.floor(Math.random() * 3);

        attack((int) Math.floor(Math.random() * 3));
    }

    public boolean allPlayersDead(){
        for(Player p: Arrays.copyOfRange(players, 0, 3)){
            if(p.getHealth()>0)
                return false;
        }
        return true;
    }

    public boolean bossDead(){
        return players[3].getHealth()<=0;
    }

    public void attack(int target) {
        Player player = players[currentPlayer];

        if (player.getAmmo() == 0) {
            actionMessage = player.getNoAmmoMessage();
            return;
        }

        Player boss = players[target];
        int damageDealt = player.shoot(boss);
        players[target] = boss;
        actionMessage = player.getShootMessage(boss.getName()) + "\n" + boss.getDamageMessage(damageDealt);

        if (boss.getHealth() <= 0) {
            actionMessage += "\n" + boss.getDeathMessage();
        }
        int initialCurrentPlayer = currentPlayer;
        currentPlayer = (currentPlayer + 1) % 4;
        while(players[currentPlayer].getHealth() <= 0) {
            currentPlayer = (currentPlayer + 1) % 4;
            //we circled around completely and there are no more people left
            if(currentPlayer == initialCurrentPlayer)
                break;
        }
        frameNum++;
    }

    public void killPlayer(int target) {
        Player curPlayer = players[target];
        curPlayer.takeDamage(players[target].getHealth());
        players[target] = curPlayer;
        actionMessage = players[target].getDeathMessage();
        //if the target is the current player, move onto the next player's turn
        if(currentPlayer == target){
            int initialCurrentPlayer = currentPlayer;
            currentPlayer = (currentPlayer + 1) % 4;
            while(players[currentPlayer].getHealth() <= 0) {
                currentPlayer = (currentPlayer + 1) % 4;
                //we circled around completely and there are no more people left
                if(currentPlayer == initialCurrentPlayer)
                    break;
            }
        }
        frameNum++;
    }

    public void defend() {
        Player player = players[currentPlayer];

        player.upDefense(10);
        players[currentPlayer] = player;
        int initialCurrentPlayer = currentPlayer;
        currentPlayer = (currentPlayer + 1) % 4;
        while(players[currentPlayer].getHealth() <= 0) {
            currentPlayer = (currentPlayer + 1) % 4;
            //we circled around completely and there are no more people left
            if(currentPlayer == initialCurrentPlayer)
                break;
        }
        actionMessage = player.getDefendMessage(10);
        frameNum++;
    }

    public void reload() {
        Player player = players[currentPlayer];

        player.reload();
        players[currentPlayer] = player;
        int initialCurrentPlayer = currentPlayer;
        currentPlayer = (currentPlayer + 1) % 4;
        while(players[currentPlayer].getHealth() <= 0) {
            currentPlayer = (currentPlayer + 1) % 4;
            //we circled around completely and there are no more people left
            if(currentPlayer == initialCurrentPlayer)
                break;
        }
        actionMessage = player.getReloadMessage();
        frameNum++;
    }

    public int getFrameNum(){return frameNum;}

    @Override
    public byte[] serialize() {
        //just serialize the game state as a whole, we'll see if this works?
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
        }catch (IOException e){
            e.printStackTrace();
        }

        return Bytes.concat(new byte[]{Integer.valueOf(5).byteValue(), Integer.valueOf(0).byteValue()},bos.toByteArray());
    }
}
