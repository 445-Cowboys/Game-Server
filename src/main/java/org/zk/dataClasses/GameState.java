package org.zk.dataClasses;

import java.util.ArrayList;

public class GameState extends ZookeeperData {

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

        int damageDealt = player.getAttack() - villain.getDefense();
        actionMessage = player.getName() + " attacks " + villain.getName();

        if (damageDealt > 0) {
            actionMessage += " for " + damageDealt + " damage.";
            villain.takeDamage(damageDealt);

        } else {
            actionMessage += " but does no damage.";
        }
    }

    public void defend() {
        Player player = players.get(currentPlayer);
        actionMessage = player.getName() + " defends and gains 10 defense power.";
        player.defend();
    }

    public void reload() {
        Player player = players.get(currentPlayer);
        actionMessage = player.getName() + " has reloaded.";
        player.reload();
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
