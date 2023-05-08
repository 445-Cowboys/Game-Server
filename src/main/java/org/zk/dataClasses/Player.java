package org.zk.dataClasses;

public class Player extends ZookeeperData {

    private final String name;
    private final int attack;
    private int defense;
    private int health;
    private final int maxAmmo;
    private int ammo;
    private final String shootMessage;
    private final String defendMessage;
    private final String reloadMessage;
    private final String damageMessage;
    private final String noAmmoMessage;
    private final String deathMessage;

    public Player(Character type) {
        this.name = type.getName();
        this.attack = type.getMaxAttack();
        this.defense = type.getMaxDefense();
        this.health = type.getMaxHealth();
        this.maxAmmo = type.getMaxAmmo();
        this.ammo = type.getMaxAmmo();
        this.shootMessage = type.getShootMessage();
        this.defendMessage = type.getDefendMessage();
        this.reloadMessage = type.getReloadMessage();
        this.damageMessage = type.getDamageMessage();
        this.noAmmoMessage = type.getNoAmmoMessage();
        this.deathMessage = type.getDeathMessage();
    }

    public String getName() {
        return name;
    }

    public int getDefense() {
        return defense;
    }

    public int getHealth() {
        return health;
    }

    public int getAmmo() {
        return ammo;
    }

    public int shoot(Player player) {
        int damageDealt = attack - player.getDefense();
        if (damageDealt < 0) { damageDealt = 0; }

        player.takeDamage(damageDealt);
        ammo--;

        return damageDealt;
    }

    public void upDefense(int amount) {
        defense += amount;
    }

    public void takeDamage(int damageDealt) {
        health -= damageDealt;
        if (health <= 0) { health = 0; }
    }

    public void reload() {
        this.ammo = maxAmmo;
    }

    public String getShootMessage(String name) {
        return this.name + shootMessage.replace("%p", name);
    }

    public String getDefendMessage(int amount) {
        return name + defendMessage.replace("%d", String.valueOf(amount));
    }

    public String getReloadMessage() {
        return name + reloadMessage;
    }

    public String getDamageMessage(int damageDealt) {
        return name + damageMessage.replace("%d", String.valueOf(damageDealt));
    }

    public String getNoAmmoMessage() {
        return name + noAmmoMessage;
    }

    public String getDeathMessage() {
        return name + deathMessage;
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
