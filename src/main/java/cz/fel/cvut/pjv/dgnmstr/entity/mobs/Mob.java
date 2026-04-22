package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.map.DroppedGold;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.logging.Logger;

public abstract class Mob extends Entity implements CanAttack {
    private final int damage;
    private final int reach;
    private final int gold;
    private static final Logger log = Logger.getLogger(Mob.class.getName());

    protected double attackCooldown = 0.0;

    public Mob(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int multiplicator) {
        super(hp  * multiplicator, speed, positionX, positionY, faction, level);
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
            log.info(this.getClass().getSimpleName() + "died and dropped " + dropGold + " gold.");
            getLevel().addEntity(new DroppedGold(getX(), getY(), getLevel(), dropGold));
        }
    }

    /**
     * makes a mob make a step to a target
     * @param target a target to make step to
     */
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

    protected Hero findNearestPlayer() {
        Hero nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Hero p : getLevel().getPlayers()){
            if (!p.isAlive()) { continue;}
            double dist = MyMath.calculateDistanceSquared(this, p);
            if (dist < minDist) {
                nearest = p;
                minDist = dist;
            }

        }
        return nearest;
    }

    protected void checkMoveToward(Entity target, double deltaTime) {
        if (target == null || !this.isAlive()) return;

        double dx = target.getX() - this.getX();
        double dy = target.getY() - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.1) {return;}

        double moveX = (dx / distance) * this.getSpeed() * deltaTime;
        double moveY = (dy / distance) * this.getSpeed() * deltaTime;

        double newX = this.getX() + moveX;
        double newY = this.getY() + moveY;

        if (isEthereal()){
            this.setPos(newX, newY);
            return;
        }

        if (getLevel().isWalkable(newX, newY)) {
            this.setPos(newX, newY);
        } else if (getLevel().isWalkable(this.getX(), newY)){
            this.setPos(this.getX(), newY);
        } else if (getLevel().isWalkable(newX, this.getY())){
            this.setPos(newX, this.getY());
        }



    }
    protected void retreatFrom(Entity target,  double deltaTime) {
        if (target == null || !this.isAlive()) {return;}

        double dx = getX() - target.getX();
        double dy = getY() - target.getY();
        double dist =  Math.sqrt(dx * dx + dy * dy);

        if (dist < 0.1) {return;}

        double moveX = (dx / dist) * getSpeed() * deltaTime;
        double moveY = (dy / dist)  * getSpeed() * deltaTime;

        double newX = getX() + moveX;
        double newY = getY() + moveY;

        if (getLevel().isWalkable(newX, newY)) {
            setPos(newX, newY);
        } else if (getLevel().isWalkable(newX, getY())) {
            setPos(newX, getY());
        } else if (getLevel().isWalkable(getX(), newY)) {
            setPos(getX(), newY);
        }
    }

}
