package cz.fel.cvut.pjv.dgnmstr.entity;

import java.util.List;

public abstract class Entity {
    private int hp;
    private int speed;
    private boolean alive;
    protected int positionX, positionY;
    private final List<HitBox> hitBox;

    public Entity(int hp, int speed, int positionX, int positionY, List<HitBox> hitBox) {
        this.hp = hp;
        this.speed = speed;
        this.alive = true;
        this.positionX = positionX;
        this.positionY = positionY;
        this.hitBox = hitBox;

    }

    public int getHp() {
        return hp;
    }

    public abstract void takeDamage(int damage);


    public void setHp(int hp) {
        this.hp = hp;
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

    public void kill() {
        this.alive = false;
        this.hp = 0;
    }

    public List<HitBox> getHitBox() {
        return hitBox;
    }
}
