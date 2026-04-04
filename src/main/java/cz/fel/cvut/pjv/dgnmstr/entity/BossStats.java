package cz.fel.cvut.pjv.dgnmstr.entity;

import java.util.List;

public enum BossStats {
    ANYSEK_THE_BEHEMOTH(1000, 10, 40, 10, 500, List.of(800, 600, 400, 200));
    private final int hp;
    private final int speed;
    private final int damage;
    private final int reach;
    private final int gold;
    private final List<Integer> threshold;

    BossStats(int hp, int speed, int damage, int reach, int gold, List<Integer> threshold) {
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.reach = reach;
        this.gold = gold;
        this.threshold = threshold;
    }

    public int getHp(double diffMod) {
        return (int) (hp * diffMod);
    }

    public int getSpeed() {
        return speed;
    }

    public int getDamage(double diffMod) {
        return (int) (damage * diffMod);
    }

    public int getReach() {
        return reach;
    }

    public int getGold() {
        return gold;
    }

    public List<Integer> getThreshold() {
        return threshold;
    }
}
