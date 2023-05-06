package org.zk.dataClasses;

import java.util.ArrayList;

public class GameState extends ZookeeperData {

    private final ArrayList<Player> players;
    private int currentPlayer;
    private String actionMessage;

    public GameState(int[] playerTypes, int currentPlayer, String actionMessage) {
        this.players = new ArrayList<>();
        this.currentPlayer = currentPlayer;
        this.actionMessage = actionMessage;

        for (int type : playerTypes) {
            this.players.add(new Player(type));
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public void playerAttack() {
        Player player = players.get(currentPlayer);
        Player villain = players.get(players.size() - 1); // villain is always the last player in the list

        if (player.getAmmo() == 0) { // make sure player can actually attack
            actionMessage = player.getName() + " has no ammo and cannot attack.";
            currentPlayer = currentPlayer + 1 % players.size();
            return;
        }

        int damageDealt = player.getAttack() - villain.getDefense();
        if (damageDealt < 0) { damageDealt = 0; }

        villain.takeDamage(damageDealt);
        player.shoot();

        actionMessage = player.getName() + " attacks " + villain.getName() + " for " + damageDealt + " damage.";
        currentPlayer = currentPlayer + 1 % players.size();
    }

    public void villainAttack() {
        Player villain = players.get(players.size() - 1); // villain is always the last player in the list
        actionMessage = villain.getName() + " attacks:";

        for (int i = 0; i < players.size() - 1; i++) { // for all players except the boss
            Player player = players.get(i);

            if (villain.getAmmo() == 0) {
                actionMessage += "\n" + villain.getName() + " has no ammo and cannot attack.";
                break;
            }

            if (player.isAlive()) { // don't want to needlessly attack dead players
                int damageDealt = villain.getAttack() - player.getDefense();
                if (damageDealt < 0) { damageDealt = 0; }

                player.takeDamage(damageDealt);
                villain.shoot();

                actionMessage += "\n" + player.getName() + " for " + damageDealt + " damage.";
            }
        }

        currentPlayer = currentPlayer + 1 % players.size();
    }

    public void defend() {
        Player player = players.get(currentPlayer);

        player.defend();

        actionMessage = player.getName() + " defends and gains 10 defense power.";
        currentPlayer = currentPlayer + 1 % players.size();
    }

    public void reload() {
        Player player = players.get(currentPlayer);

        player.reload();

        actionMessage = player.getName() + " has reloaded.";
        currentPlayer = currentPlayer + 1 % players.size();
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
