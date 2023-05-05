package org.zk.dataClasses;

import java.util.ArrayList;

public class Player extends ZookeeperData {

    private PlayerType type;
    private String name;
    private int health;
    private int attack;
    private int defense;
    private int ammo;
    private boolean isAlive;

    public Player(int type) {
        this.type = PlayerType.values()[type];
        this.name = this.type.getName();
        this.health = this.type.getHealth();
        this.attack = this.type.getAttack();
        this.defense = this.type.getDefense();
        this.ammo = 6;
        this.isAlive = true;
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

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
