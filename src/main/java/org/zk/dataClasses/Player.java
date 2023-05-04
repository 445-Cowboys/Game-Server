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

    public String attack(Player villain) {
        int damage = this.attack - villain.getDefense();
        if (damage > 0) {
            villain.takeDamage(damage);
            return this.name + " attacks " + villain.getName() + " for " + damage + " damage.";
        } else {
            return this.name + " attacks " + villain.getName() + " but does no damage.";
        }
    }

    public String defend() {
        this.defense += 10;
        return this.name + " defends and gains 10 defense power.";
    }

    public String reload() {
        this.ammo = 6;
        return this.name + " has reloaded.";
    }

    public String takeDamage(int damage) {
        this.health -= damage;
        String message = this.name + " has taken " + damage + "points of damage.";
        if (this.health <= 0) {
            this.isAlive = false;
            message += "\n" + this.name + " have succumbed to their wounds and fallen!";
        }
        return message;
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

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
