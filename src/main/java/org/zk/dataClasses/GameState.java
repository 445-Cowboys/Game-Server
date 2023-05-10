package org.zk.dataClasses;

public class GameState extends ZookeeperData {

    private final Player[] players;
    private int numPlayers;
    private int currentPlayer;
    private String actionMessage;

    public GameState() {
        this.players = new Player[4];
        this.numPlayers = 0;
        this.currentPlayer = 0;

        this.players[3] = new Player(Character.getRandomBoss());
    }

    public void addPlayer() {
        players[numPlayers] = new Player(Character.getPlayer(numPlayers));
        numPlayers++;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public void bossTurn() {
        Player boss = players[3];

        if ((int) Math.floor(Math.random() * 4) == 3) {
            defend();
            return;
        }

        if (boss.getAmmo() == 0) {
            reload();
            return;
        }

        attack((int) Math.floor(Math.random() * 3));
    }

    public void attack(int target) {
        Player player = players[currentPlayer];
        currentPlayer = currentPlayer + 1 % 4;

        if (player.getAmmo() == 0) {
            actionMessage = player.getNoAmmoMessage();
            return;
        }

        Player boss = players[target];
        int damageDealt = player.shoot(boss);

        actionMessage = player.getShootMessage(boss.getName()) + "\n" + boss.getDamageMessage(damageDealt);

        if (boss.getHealth() <= 0) {
            actionMessage += "\n" + boss.getDeathMessage();
        }
    }

    public void defend() {
        Player player = players[currentPlayer];
        currentPlayer = currentPlayer + 1 % 4;

        player.upDefense(10);

        actionMessage = player.getDefendMessage(10);
    }

    public void reload() {
        Player player = players[currentPlayer];
        currentPlayer = currentPlayer + 1 % 4;

        player.reload();

        actionMessage = player.getReloadMessage();
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
