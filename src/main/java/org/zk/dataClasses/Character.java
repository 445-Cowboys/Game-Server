package org.zk.dataClasses;

public enum Character {

    THE_MAN_WITH_SOME_NAME("The Man With Some Name",
            100, 10, 100, 6,
            "shoots %p with his revolver",
            "takes a swig of whiskey and gains %d defense",
            "reloads his revolver",
            "takes a shot worth %d damage",
            "is out of ammo",
            "succumbs to his wounds"),

    ONE_THOUSAND_GRIT_SANDPAPER("1000 Grit Sandpaper",
            100, 10, 10, 6,
            "shoots %p with his sandpaper shooter",
            "puts on a new layer of sandpaper and gains %d defense",
            "reloads his sandpaper shooter",
            "gets a paper cut for %d damage",
            "is out of sandpaper",
            "is covered in paper cuts and dies"),

    JANGO_THE_LIBERATOR("Jango the Liberator",
            100, 10, 100, 6,
            "shoots %p with his blaster",
            "gains %d defense from his blaster shield",
            "reloads his blaster",
            "endures %d damage",
            "is out of blaster ammo",
            "is killed by a stray blaster bolt"),

    DOUG_LEA("Doug Lea",
            10, 10, 100, 6,
            "shoots %p with two shotguns at once",
            "escapes into a thread for an extra %d defense",
            "reloads his shotguns in parallel",
            "drops %d of his packets",
            "has run out of threads",
            "is killed by a thread deadlock"),

    ANTHILL_SUGAR("Anthill Sugar",
            10, 10, 100, 6,
            "shoots %p with his ant cannon",
            "gains %d defense from his ant hill",
            "reloads his ant cannon",
            "loses %d ants to the anteater",
            "is out of ants",
            "is eaten by an anteater"),

    DEMON_FEET("Demon Feet",
            10, 10, 100, 6,
            "shoots %p with his demon gun",
            "gains %d defense from his cursed feet",
            "reloads his demon gun",
            "gets hit with %d holy damage",
            "is out of demonic power",
            "is exorcised by a priest");

    private static final Character[] players = {
            THE_MAN_WITH_SOME_NAME,
            ONE_THOUSAND_GRIT_SANDPAPER,
            JANGO_THE_LIBERATOR
    };

    private static final Character[] bosses = {
            DOUG_LEA,
            ANTHILL_SUGAR,
            DEMON_FEET
    };

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
    private final String deathMessage;

    Character(String name,
              int maxAttack, int maxDefense, int maxHealth, int maxAmmo,
              String shootMessage,
              String defendMessage,
              String reloadMessage,
              String damageMessage,
              String noAmmoMessage,
              String deathMessage) {

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
        this.deathMessage = deathMessage;
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

    public String getDeathMessage() {
        return deathMessage;
    }
}
