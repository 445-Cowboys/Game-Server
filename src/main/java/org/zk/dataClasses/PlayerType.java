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
    private final int health;
    private final int attack;
    private final int defense;

    PlayerType(String name, int health, int attack, int defense) {
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }
}
