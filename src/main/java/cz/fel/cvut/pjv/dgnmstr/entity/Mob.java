package cz.fel.cvut.pjv.dgnmstr.entity;

import java.util.List;

public abstract class Mob extends Entity{
    private int damage, reach, gold;

    public Mob(int hp, int speed, int positionX, int positionY, List<HitBox> hitBox, int damage, int reach, int gold) {
        super(hp, speed, positionX, positionY, hitBox);
        this.damage = damage;
        this.reach = reach;
        this.gold = gold;
    }

    @Override
    public void takeDamage(int damage) {
        int hp = getHp();
        if (isAlive()) hp -= damage;
        if (hp <= 0) kill();
    }
}
