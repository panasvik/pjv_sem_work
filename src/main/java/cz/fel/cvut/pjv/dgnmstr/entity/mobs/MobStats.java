package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public enum MobStats {
    ZOMBIE(70, 1, 20, 20, 50) {
        @Override
        public Mob spawnWithMult(int id, double x, double y, Level level, int mult) {
            return new Zombie(id, x, y, level, mult);
        }
    },
    GHOST(50, 1, 15, 20, 100) {
        @Override
        public Mob spawnWithMult(int id, double x, double y, Level level, int mult) {
            return new Ghost(id, x, y, level, mult);
        }
    },
    EVIL_WIZARD (30, 1, 25, 400, 150) {
        @Override
        public Mob spawnWithMult(int id, double x, double y, Level level, int mult) {
            return new EvilWizard(id, x, y, level, mult);
        }
    },
    GOBLIN(50, 2, 15, 20, 50) {
        @Override
        public Mob spawnWithMult(int id, double x, double y, Level level, int mult) {
            return new Goblin(id, x, y, level, mult);
        }
    },
    SKELETON(40, 1, 20, 300, 75){
        @Override
        public Mob spawnWithMult(int id, double x, double y, Level level, int mult) {
            return new Skeleton(id, x, y, level, mult);
        }
    };



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

    public abstract Mob spawnWithMult(int id, double x, double y, Level level, int mult);

    public Mob spawn(int id, double x, double y, Level level) {
        return this.spawnWithMult(id, x, y, level, 1);
    }
}

