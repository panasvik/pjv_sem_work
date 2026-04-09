package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public enum MobStats {
    ZOMBIE(70, 10, 20, 5, 50) {
        @Override
        public Mob spawnWithMult(double x, double y, Level level, int mult) {
            return new Zombie(x, y, level, mult);
        }
    },
    GHOST(50, 15, 15, 5, 100) {
        @Override
        public Mob spawnWithMult(double x, double y, Level level, int mult) {
            return new Ghost(x, y, level, mult);
        }
    },
    EVIL_WIZARD (30, 10, 25, 40, 150) {
        @Override
        public Mob spawnWithMult(double x, double y, Level level, int mult) {
            return new EvilWizard(x, y, level, mult);
        }
    };//reach má být neomezen nezapomenout

    private final int hp;
    private final int speed;
    private final int damage;
    private final int reach;
    private final int gold;

    MobStats(int hp, int speed, int damage, int reach, int gold) {
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.reach = reach;
        this.gold = gold;
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

    public abstract Mob spawnWithMult(double x, double y, Level level, int mult);

    public Mob spawn(double x, double y, Level level) {
        return this.spawnWithMult(x, y, level, 1);
    }
}

