package cz.fel.cvut.pjv.dgnmstr.dungeon.service;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Zombie;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;

import java.util.List;
import java.util.logging.Logger;

public enum ArenaType {
    GOBLIN_CAMP(List.of(
            new MobSpawnRate(MobStats.ZOMBIE, 0.3),
            new MobSpawnRate(MobStats.GOBLIN, 0.5),
            new MobSpawnRate(MobStats.EVIL_WIZARD,0.2)
    )),
    UNDEAD_CRYPT(List.of(
            new MobSpawnRate(MobStats.ZOMBIE, 0.3),
            new MobSpawnRate(MobStats.SKELETON, 0.4),
            new MobSpawnRate(MobStats.GHOST, 0.3)
    )),
    SPIDER_NEST(List.of(
            new MobSpawnRate(MobStats.GHOST, 0.3),
            new MobSpawnRate(MobStats.GOBLIN, 0.4),
            new MobSpawnRate(MobStats.SKELETON, 0.3)
    )),
    FORGE(List.of(
            new MobSpawnRate(MobStats.EVIL_WIZARD, 0.3),
            new MobSpawnRate(MobStats.GOBLIN, 0.3),
            new MobSpawnRate(MobStats.ZOMBIE, 0.4)
    )),
    ROYAL_TOMB(List.of(
            new MobSpawnRate(MobStats.EVIL_WIZARD, 0.5),
            new MobSpawnRate(MobStats.SKELETON, 0.1),
            new MobSpawnRate(MobStats.GHOST, 0.2),
            new MobSpawnRate(MobStats.ZOMBIE, 0.2)

    ));
    private static final Logger log = Logger.getLogger(ArenaType.class.getName());
    private static final int miniBossMult = 3;

    private final List<MobSpawnRate> mobSpawnRates;

    ArenaType(List<MobSpawnRate> mobSpawnRates) {
        this.mobSpawnRates = mobSpawnRates;
    }




    public Mob spawnRandomMob(double x, double y, Level level) {
        double chance = Math.random();
        double sum = 0;
        for (MobSpawnRate rate : mobSpawnRates) {
            sum += rate.chance();
            if (chance <= sum) {
                int id = GameManager.getInstance().getIdGenerator().getNextId();
                Mob spawned = rate.type().spawn(id, x, y, level);
                log.fine("Spawned " + spawned.getClass().getSimpleName() + " at [" + x + ", " + y + "]");
                return spawned;
            }
        }
        int id = GameManager.getInstance().getIdGenerator().getNextId();
        return new Zombie(id, x, y, level);
    }

    public Mob spawnMiniBoss(double x, double y, Level level) {
        double chance = Math.random();
        double sum = 0;
        for (MobSpawnRate rate : mobSpawnRates) {
            sum += rate.chance();
            if (chance <= sum) {
                int id = GameManager.getInstance().getIdGenerator().getNextId();

                Mob spawned = rate.type().spawnWithMult(id, x, y, level, miniBossMult);
                log.fine("Miniboss " + spawned.getClass().getSimpleName() + " spawned at [" + x + ", " + y + "]");
                return spawned;
            }
        }
        int id = GameManager.getInstance().getIdGenerator().getNextId();

        return new Zombie(id, x, y, level, miniBossMult);
    }
}