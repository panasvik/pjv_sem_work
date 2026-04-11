package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;

public abstract class Mob extends Entity implements CanAttack {
    private final int damage;
    private final int reach;
    private final int gold;

    public Mob(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int multiplicator, int id) {
        super(hp  * multiplicator, speed, positionX, positionY, faction, level, id);
        this.damage = damage * multiplicator;
        this.reach = reach;
        this.gold = gold * multiplicator;
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
            getLevel().addEntity(new DroppedGold(getX(), getY(), getLevel(), dropGold));
        }
    }

    public void moveToward(Entity target) {
        if(target == null || !this.isAlive()) return;

        double dx = target.getX() - this.getX();
        double dy = target.getY() - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > 0) {
            double moveX = (dx / distance) * this.getSpeed();
            double moveY = (dy / distance) * this.getSpeed();

            this.setPos(this.getX() + moveX, this.getY() + moveY);
        }
    }

}
