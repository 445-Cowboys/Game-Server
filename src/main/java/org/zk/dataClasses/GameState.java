package org.zk.dataClasses;

import java.util.ArrayList;

public class GameState extends ZookeeperData{
    //Convening with Tom tomorrow before designing this!

    ArrayList<Player> players;
    int currentPlayer;
    String actionMessage;

    public GameState(int[] playerTypes, int currentPlayer, String actionMessage) {
        this.players = new ArrayList<>();
        this.currentPlayer = currentPlayer;
        this.actionMessage = actionMessage;

        for (int type : playerTypes) {
            this.players.add(new Player(type));
        }
    }

    public void attack() {
        Player player = players.get(currentPlayer);
        Player villain = players.get(3);

        actionMessage = player.attack(villain);
    }

    public void defend() {
        Player player = players.get(currentPlayer);
        actionMessage = player.defend();
    }

    public void reload() {
        Player player = players.get(currentPlayer);
        actionMessage = player.reload();
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
