package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.DungeonGenerator;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.TestSpawner;
import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.LevelController;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.DifficultyStats;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {
    LevelController levelController;
    LevelController lc;
    Level level;
    Mob zombie;
    @BeforeEach
    void setUp() {
        DungeonGenerator generator = new DungeonGenerator();
        long testSeed = 12345678L;
        level = generator.generate(DifficultyStats.MEDIUM, testSeed);
        IDGenerator gen = new IDGenerator();
        TestSpawner testSpawner = new TestSpawner(level, gen);
        level.setSpawner(testSpawner);
        lc = new LevelController(level);
        lc.startLevel();
        zombie = level.requestMobSpawn(128, 128, MobStats.ZOMBIE, 1);
    }


    @Test
    void setHp() {
        int exHP = 0;
        zombie.setHp(-20000);
        assertEquals(exHP, zombie.getHp());
    }

    @Test
    void tryStepTo() {
        System.out.println(lc.getArenaController().getArena().getType().name());
        for (int i = 0; i < 1000; i++) {
            zombie.tryStepTo(-1, -1);
        }
        System.out.println("x: " + zombie.getX() + " y: " + zombie.getY());
        assertTrue(zombie.getX() > 0 && zombie.getY() > 0);
    }


}