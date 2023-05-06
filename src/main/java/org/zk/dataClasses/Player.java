package org.zk.dataClasses;

public class Player extends ZookeeperData {

    private final String name;
    private int health;
    private final int attack;
    private int defense;
    private int ammo;
    private boolean isAlive;

    public Player(int typeNumber) {
        PlayerType type = PlayerType.values()[typeNumber];
        this.name = type.getName();
        this.health = type.getMaxHealth();
        this.attack = type.getMaxAttack();
        this.defense = type.getMaxDefense();
        this.ammo = 6;
        this.isAlive = true;
    }

    public String getName() {
        return name;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getAmmo() {
        return ammo;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void takeDamage(int damage) {
        health = health - damage;

        if (health <= 0) {
            isAlive = false;
            health = 0;
        }
    }

    public void defend() {
        defense += 10;
    }

    public void shoot() {
        ammo--;
    }

    public void reload() {
        this.ammo = 6;
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
