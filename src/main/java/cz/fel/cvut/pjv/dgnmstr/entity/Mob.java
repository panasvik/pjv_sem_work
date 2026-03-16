package cz.fel.cvut.pjv.dgnmstr.entity;

import java.util.List;

public abstract class Mob extends Entity implements CanAttack {
    private final int damage;
    private final int reach;
    private final int gold;

    public Mob(int maxHP, int hp, int speed, int positionX, int positionY, int damage, int reach, int gold) {
        super(maxHP, hp, speed, positionX, positionY);
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
}
