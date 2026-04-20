package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.List;
import java.util.logging.Logger;

public class Projectile implements Updatable, Positionable {
    private final int radius;
    private final Entity master;
    private final int damage;
    private double deltaX;
    private double deltaY;
    private double posX;
    private double posY;
    private final double velocity;
    private static final Logger log = Logger.getLogger(Projectile.class.getName());
    private final Level level;
    private boolean alive = true;

    public Projectile(int radius, double posX, double posY, Entity master, int damage, double deltaX, double deltaY, double velocity) {
        this.radius = radius;
        this.posY = posY;
        this.posX = posX;
        this.master = master;
        this.damage = damage;
        calculateDelta(deltaX, deltaY);
        this.velocity = velocity;
        this.level = master.getLevel();
    }

    private void calculateDelta(double deltaX, double deltaY) {
        double dist = Math.sqrt(MyMath.calculateDistanceSquared(deltaX, deltaY));
        if (dist > 0) {
            this.deltaX = deltaX / dist;
            this.deltaY = deltaY / dist;
        } else {
            this.deltaX = 0;
            this.deltaY = 0;
        }

        this.deltaX = deltaX / Math.sqrt(MyMath.calculateDistanceSquared(deltaX, deltaY));
        this.deltaY = deltaY / Math.sqrt(MyMath.calculateDistanceSquared(deltaX, deltaY));
    }
    @Override
    public double getX() {
        return posX;
    }

    @Override
    public double getY() {
        return posY;
    }

    public int getRadius() {
        return radius;
    }

    public void setPos(double posX, double posY) {
        this.posY = posY;
        this.posX = posX;
    }

    public Entity getMaster() {
        return master;
    }

    public int getDamage() {
        return damage;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getVelocity() {
        return velocity;
    }

    public void explodeOn(List<Entity> targets) {
        if (targets != null) {
            for (Entity target : targets) {
                target.takeDamage(damage, master);
            }
        }
        alive = false;
    }

    @Override
    public void update(double deltaTime) {
        if (!alive) return;
        posX += deltaX * velocity;
        posY += deltaY * velocity;
        List<Entity> entNear = level.getEntitiesAround(this, radius);
        entNear = entNear.stream()
                .filter(Entity::isAlive)
                .filter(e -> e != master)
                .filter(master::isEnemy)
                .toList();
        if (!entNear.isEmpty()) {
            explodeOn(entNear);
            return;
        }
        if (!level.isWalkable(posX, posY)) {
            explodeOn(null);
        }
    }

    public boolean isAlive() {
        return alive;
    }

}
