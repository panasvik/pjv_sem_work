package cz.fel.cvut.pjv.dgnmstr.entity;

public class Projectile {
    private final int radius;
    private final Entity master;
    private final int damage;
    private double deltaX;
    private double deltaY;
    private double posX;
    private double posY;
    private double velocity;

    public Projectile(int radius, double posX, double posY, Entity master, int damage, double deltaX, double deltaY, double velocity) {
        this.radius = radius;
        this.posY = posY;
        this.posX = posX;
        this.master = master;
        this.damage = damage;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.velocity = velocity;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
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

    public void explodeOn(Entity target) {
        target.takeDamage(damage, master);
        master.getLevel().removeProjectile(this);
    }

    public void update() {

    }
}
