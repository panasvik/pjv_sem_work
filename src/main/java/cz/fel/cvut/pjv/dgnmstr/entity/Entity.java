package cz.fel.cvut.pjv.dgnmstr.entity;

import java.util.List;

public abstract class Entity {
    private final int maxHP;
    private int hp;
    private int speed;
    private boolean alive;
    protected int positionX, positionY;

    public Entity(int maxHP, int hp, int speed, int positionX, int positionY) {
        this.maxHP = maxHP;
        this.hp = hp;
        this.speed = speed;
        this.alive = true;
        this.positionX = positionX;
        this.positionY = positionY;

    }

    public int getHp() {
        return hp;
    }

    public final void takeDamage(int damage, Entity source) {
        if (damage <= 0) return;
        if (handleParry(source)) return;
        damage = applyDefence(damage);
        this.hp -= damage;
        onDamageTaken(damage, source);
        if (this.hp <= 0) {
            die();
        }
    }

    protected abstract void onDamageTaken(int amount, Entity source);

    protected boolean handleParry(Entity source) {
        return !(source instanceof CanAttack);
    }

    protected abstract int applyDefence(int damage);

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

    public void die() {
        this.alive = false;
        this.hp = 0;
    }


    public int getMaxHP() {
        return maxHP;
    }

    public int getX() {
        return positionX;
    }

    public int getY() {
        return positionY;
    }
}
