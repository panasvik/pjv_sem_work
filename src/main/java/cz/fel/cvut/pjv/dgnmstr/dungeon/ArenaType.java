package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.*;

import java.util.List;

public enum ArenaType {
    GOBLIN_CAMP(List.of(
            new MobSpawnRate(MobStats.ZOMBIE, 0.7),
            new MobSpawnRate(MobStats.GHOST, 0.3)
    ));
//  UNDEAD_CRYPT,
//  SPIDER_NEST,
//  FORGE,
//  ROYAL_TOMB;

    private static final int miniBossMult = 3;

    private final List<MobSpawnRate> mobSpawnRates;
//    private final List<BossSpawnRate> bossSpawnRates;

    ArenaType(List<MobSpawnRate> mobSpawnRates) {
        this.mobSpawnRates = mobSpawnRates;
//        this.bossSpawnRates = bossSpawnRates;
    }




    public Mob spawnRandomMob(double x, double y, Level level) {
        double chance = Math.random();
        double sum = 0;
        for (MobSpawnRate rate : mobSpawnRates) {
            sum += rate.chance();
            if (chance <= sum) {
                return rate.type().spawn(x, y, level);
            }
        }
        return new Zombie(x, y, level);
    }

    public Mob spawnMiniBoss(double x, double y, Level level) {
        double chance = Math.random();
        double sum = 0;
        for (MobSpawnRate rate : mobSpawnRates) {
            sum += rate.chance();
            if (chance <= sum) {
                return rate.type().spawnWithMult(x, y, level, miniBossMult);
            }
        }
        return new Zombie(x, y, level, miniBossMult);
    }
}