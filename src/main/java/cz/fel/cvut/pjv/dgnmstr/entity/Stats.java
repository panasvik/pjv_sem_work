package cz.fel.cvut.pjv.dgnmstr.entity;

import static cz.fel.cvut.pjv.dgnmstr.entity.Faction.*;
/**
 * Predefined immutable stat presets for special, non-combat entities.
 */
public enum Stats {
    FENCE(50, 0, NEUTRAL, 14),
    GOD(1, 0, NEUTRAL, 10);
    private final int health;
    private final int speed;
    private final Faction faction;
    private final double halfHitBox;

    Stats(int health, int speed, Faction faction, double halfHitBox) {
        this.health = health;
        this.speed = speed;
        this.faction = faction;
        this.halfHitBox = halfHitBox;

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

    public double getHalfHitBox() {
        return halfHitBox;
    }
}
