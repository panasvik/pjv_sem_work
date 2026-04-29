package cz.fel.cvut.pjv.dgnmstr.entity;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanAttack;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Positionable;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Updatable;
import cz.fel.cvut.pjv.dgnmstr.network.ServerManager;
/**
 * All entities should be based on this class
 * provides basic features of Entity:
 * health, speed, position, level where at, id
 *
 * */

import java.util.logging.Logger;

public abstract class Entity implements Updatable, Positionable {
    private int maxHP;
    private int hp;
    private int speed;
    private boolean alive;
    private double positionX, positionY;
    private double lookX, lookY;
    private final Faction faction;
    private final Level level;
    private final int id;
    private static final Logger log = Logger.getLogger(Entity.class.getName());

    public Entity(int id, int hp, int speed, double positionX, double positionY, Faction faction, Level level) {
        this.maxHP = hp;
        this.hp = hp;
        this.speed = speed;
        this.alive = true;
        this.positionX = positionX;
        this.positionY = positionY;
        this.faction = faction;
        this.level = level;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getHp() {
        return hp;
    }

    /**
     * logic for taking damage
     * @param damage how much damage to take
     * @param source who deals damage to this
     */
    public final void takeDamage(int damage, Entity source) {
        if (damage <= 0) return;
        if (handleParry(source)) return;
        damage = applyDefence(damage);
        this.hp -= damage;
        log.fine(this.getClass().getSimpleName() + ": took " + damage + " damage. HP left: " + this.hp );
        onDamageTaken(damage, source);
        if (this.hp <= 0) {
            die();
        }
    }

    protected abstract void onDamageTaken(int amount, Entity source);


    protected boolean handleParry(Entity source) {
        return !(source instanceof CanAttack);
    }

    /**
     * decides how much damage will this take
     * @param damage base damage
     * @return how much will take
     */
    protected abstract int applyDefence(int damage);

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(hp, this.maxHP));
        if (hp < 0) {
            die();
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void die() {
        this.alive = false;
        this.hp = 0;
    }


    public int getMaxHP() {
        return maxHP;
    }

    public double getX() {
        return positionX;
    }

    public double getY() {
        return positionY;
    }

    public void setPos(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public boolean isEnemy(Entity other) {
        if (this.faction == Faction.NEUTRAL) {
            return false;
        }
        return this.faction != other.faction;
    }

    /**
     * sets the target position to look at
     * @param targetX target X coords
     * @param targetY target Y coords
     */
    public void setLookTarget(double targetX, double targetY) {
        this.lookX = targetX;
        this.lookY = targetY;
    }

    public double getLookX() {
        return lookX;
    }

    public double getLookY() {
        return lookY;
    }

    public Level getLevel() {
        return level;
    }

    protected void setMaxHP(int maxHP) {
        this.maxHP = Math.max(0, maxHP);
    }

    /**
     * can walk thorough walls or not
     * @return true can / false cannot
     */
    public boolean isEthereal() {
        return false;
    }

    /**
     * can this entity be walked through
      * @return true can / false cannot
     */
    public boolean isSolid() {
        return false;
    }

    public Faction getFaction() {
        return faction;
    }

//    public abstract void acceptServer(ServerManager sm);
}
