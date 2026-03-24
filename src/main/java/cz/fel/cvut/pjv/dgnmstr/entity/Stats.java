package cz.fel.cvut.pjv.dgnmstr.entity;

import static cz.fel.cvut.pjv.dgnmstr.entity.Faction.*;

public enum Stats {
    FENCE(50, 0, NEUTRAL),
    GOD(1, 0, NEUTRAL);
    private int health;
    private int speed;
    private Faction faction;

    Stats(int health, int speed, Faction faction) {
        this.health = health;
        this.speed = speed;
        this.faction = faction;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public Faction getFaction() {
        return faction;
    }
}
