package cz.fel.cvut.pjv.dgnmstr.entity.boss;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;

public enum BossStats {
    ANYSEK_THE_BEHEMOTH(1000, 5, 100, 10, 500, 48, List.of(800, 600, 400, 200)) {
        @Override
        public Boss spawn(int id, double x, double y, Level level) {
            return new AnysekTheBehemoth(id, x, y, level);
        }
    },
    DWIGHT_THE_LEVIATHAN(800, 4, 100, 400, 500, 48, List.of(600, 400, 200)) {
        @Override
        public Boss spawn(int id, double x, double y, Level level) {
            return new DwightTheLeviathan(id, x, y, level);
        }
    };
    private final int hp;
    private final int speed;
    private final int damage;
    private final int reach;
    private final int gold;
    private final double halfHitBox;
    private final List<Integer> threshold;

    BossStats(int hp, int speed, int damage, int reach, int gold, double halfHitBox, List<Integer> threshold) {
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.reach = reach;
        this.gold = gold;
        this.threshold = threshold;
        this.halfHitBox = halfHitBox;
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

    public abstract Boss spawn(int id, double x, double y,  Level level);

    public double getHalfHitBox() {
        return halfHitBox;
    }
}
