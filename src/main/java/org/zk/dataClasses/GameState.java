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
        Player villain = players.get(players.size() - 1); // villain is always the last player in the list
        currentPlayer = currentPlayer + 1 % players.size();

        int damageDealt = player.getAttack() - villain.getDefense();
        actionMessage = player.getName() + " attacks " + villain.getName();

        if (damageDealt > 0) {
            actionMessage += " for " + damageDealt + " damage.";
            villain.takeDamage(damageDealt);

        } else {
            actionMessage += " but does no damage.";
        }

        if (currentPlayer == players.size() - 1) { // the next turn is the boss turn
            for (int i = 0; i < players.size() - 1; i++) { // for all players except the boss
                Player p = players.get(i);

                if (p.isAlive()) { // don't want to needlessly attack dead players
                    int damageDealtToPlayer = villain.getAttack() - p.getDefense();
                    actionMessage += "\n" + villain.getName() + " attacks " + p.getName();

                    if (damageDealtToPlayer > 0) {
                        actionMessage += " for " + damageDealtToPlayer + " damage.";
                        p.takeDamage(damageDealtToPlayer);

                    } else {
                        actionMessage += " but does no damage.";
                    }
                }
            }

            currentPlayer = currentPlayer + 1 % players.size();
        }
    }

    public void defend() {
        Player player = players.get(currentPlayer);
        currentPlayer = currentPlayer + 1 % players.size();

        actionMessage = player.getName() + " defends and gains 10 defense power.";
        player.defend();
    }

    public void reload() {
        Player player = players.get(currentPlayer);
        currentPlayer = currentPlayer + 1 % players.size();

        actionMessage = player.getName() + " has reloaded.";
        player.reload();
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
