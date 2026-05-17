package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanAttack;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;

import java.util.logging.Logger;

/**
 * Base class for enemy creatures. Handles aggro, simple movement
 * towards the player and gold drop on death.
 */
public abstract class Mob extends Entity implements CanAttack {
    private final int damage;
    private final int reach;
    private final int gold;
    private static final Logger log = Logger.getLogger(Mob.class.getName());

    protected double attackCooldown = 0.0;
    /**
     * Creates a new Mob.
     * @param id unique identifier
     * @param hp hit points value
     * @param speed movement speed
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param halfHitBox half the collision box size
     * @param damage amount of damage
     * @param reach reach
     * @param gold amount of gold
     * @param faction side this belongs to
     * @param level the level this belongs to
     * @param multiplicator multiplicator
     */
    public Mob(int id, int hp, int speed, double positionX, double positionY, double halfHitBox, int damage, int reach, int gold, Faction faction, Level level, int multiplicator) {
        super(id,hp  * multiplicator, speed, positionX, positionY, halfHitBox, faction, level);
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

            getLevel().requestGoldSpawn(getX(), getY(), dropGold);
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

    /**
     * makes a mob make a step to a target
     * @param target a target to make step to
     */
    protected void checkMoveToward(Entity target) {
        if (target == null || !this.isAlive()) return;

        double dx = target.getX() - this.getX();
        double dy = target.getY() - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.1) {return;}

        double moveX = (dx / distance) * this.getSpeed();
        double moveY = (dy / distance) * this.getSpeed();

        tryStepTo(moveX, moveY);
    }

    protected void retreatFrom(Entity target) {
        if (target == null || !this.isAlive()) {return;}

        double dx = getX() - target.getX();
        double dy = getY() - target.getY();
        double dist =  Math.sqrt(dx * dx + dy * dy);

        if (dist < 0.1) {return;}

        double moveX = (dx / dist) * getSpeed();
        double moveY = (dy / dist)  * getSpeed();

        double newX = getX() + moveX;
        double newY = getY() + moveY;

        double newCenterX = this.getX() + moveX;
        double newCenterY = this.getY() + moveY;
        double half = this.getHalfHitBox();

        if (getLevel().isWalkable(newCenterX, newCenterY, half)) {
            setPos(newX, newY);
        } else if (getLevel().isWalkable(newCenterX, getY(), half)) {
            setPos(newX, getY());
        } else if (getLevel().isWalkable(getX(), newCenterY, half)) {
            setPos(getX(), newY);
        }
    }

}
