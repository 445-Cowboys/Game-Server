package org.zk.dataClasses;

public enum PlayerType {

    // Feel free to help with the naming, I will come back to this
    PLAYER_ONE("PlayerOne", 100, 10, 10),
    PLAYER_TWO("PlayerTwo", 100, 10, 10),
    PLAYER_THREE("PlayerThree", 100, 10, 10),
    VILLAIN_ONE("VillainOne", 100, 10, 10),
    VILLAIN_TWO("VillainTwo", 100, 10, 10),
    VILLAIN_THREE("VillainThree", 100, 10, 10);

    private final String name;
    private final int maxHealth;
    private final int maxAttack;
    private final int maxDefense;

    PlayerType(String name, int maxHealth, int maxAttack, int maxDefense) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.maxAttack = maxAttack;
        this.maxDefense = maxDefense;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMaxAttack() {
        return maxAttack;
    }

    public int getMaxDefense() {
        return maxDefense;
    }
}
