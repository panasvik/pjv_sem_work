package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;

public abstract class Mob extends Entity implements CanAttack {
    private int damage;
    private int reach;
    private int gold;

    public Mob(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level) {
        super(hp, speed, positionX, positionY, faction, level);
        this.damage = damage;
        this.reach = reach;
        this.gold = gold;
    }

    @Override
    public int getDamage() {
        return damage;
    }


    @Override
    protected int applyDefence(int damage) {
        return damage;
    }


    public int getReach() {
        return reach;
    }

    public int getGold() {
        return gold;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setReach(int reach) {
        this.reach = reach;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
}
