package org.zk.dataClasses;

public enum Character {

    LONE_RANGER("Lone Ranger",
            10, 10, 100, 6,
            "unloads his justice on you",
            "shields himself with his hat",
            "reloads his six-shooter in a blink",
            "takes a bullet to the knee",
            "is out of ammo"),

    DOUG_LEA("Doug Lea",
            10, 10, 100, 5,
            "shoots %p with two shotguns at once",
            "escapes into a thread for an extra %d defense",
            "reloads his shotguns in parallel",
            "drops %d of his packets",
            "has run out of threads");

    private static final Character[] players = { LONE_RANGER };
    private static final Character[] bosses = { DOUG_LEA };

    private final String name;
    private final int maxAttack;
    private final int maxDefense;
    private final int maxHealth;
    private final int maxAmmo;
    private final String shootMessage;
    private final String defendMessage;
    private final String reloadMessage;
    private final String damageMessage;
    private final String noAmmoMessage;

    Character(String name,
              int maxAttack, int maxDefense, int maxHealth, int maxAmmo,
              String shootMessage, String defendMessage, String reloadMessage, String damageMessage, String noAmmoMessage) {

        this.name = name;
        this.maxAttack = maxAttack;
        this.maxDefense = maxDefense;
        this.maxHealth = maxHealth;
        this.maxAmmo = maxAmmo;
        this.shootMessage = shootMessage;
        this.defendMessage = defendMessage;
        this.reloadMessage = reloadMessage;
        this.damageMessage = damageMessage;
        this.noAmmoMessage = noAmmoMessage;
    }

    public static Character getPlayer(int index) {
        return players[index];
    }

    public static Character getRandomBoss() {
        return bosses[(int) Math.floor(Math.random() * bosses.length)];
    }

    public String getName() {
        return name;
    }

    public int getMaxAttack() {
        return maxAttack;
    }

    public int getMaxDefense() {
        return maxDefense;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public String getShootMessage() {
        return shootMessage;
    }

    public String getDefendMessage() {
        return defendMessage;
    }

    public String getReloadMessage() {
        return reloadMessage;
    }

    public String getDamageMessage() {
        return damageMessage;
    }

    public String getNoAmmoMessage() {
        return noAmmoMessage;
    }
}
