package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;

public abstract class Mob extends Entity implements CanAttack {
    private final int damage;
    private final int reach;
    private final int gold;

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

    @Override
    public void die() {
        super.die();
        int dropGold = (int)(this.getGold() * (Math.random() + 0.5));

        if (getLevel() != null){
            getLevel().removeEntity(this);

            getLevel().addEntity(new DroppedGold(getX(), getY(), getLevel(), dropGold));
        }

    }
}
