package cz.fel.cvut.pjv.dgnmstr.dungeon.controllers;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Arena;
import cz.fel.cvut.pjv.dgnmstr.dungeon.DungeonGenerator;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.TestSpawner;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.DifficultyStats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Role;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ArenaControllerTest {
    LevelController lc;
    Level level;
    ArenaController ac;

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
        ac = new ArenaController(level.getCurArena(), level);
    }

    @Test
    void playerEnters() {
        Arena arena = ac.getArena();
        assertEquals(ArenaState.NOT_STARTED, arena.getState());

        PlayerProfile profile = new PlayerProfile(Role.WARRIOR);
        level.getSpawner().spawnHero(arena.getCenterWorldX(), arena.getCenterWorldY(), profile);

        ac.update(16.0);

        assertEquals(ArenaState.WAVE_FIGHT, arena.getState());
        assertFalse(ac.getGrates().isEmpty());
    }

    @Test
    void closeGrates() {
        ArenaController testAC = new ArenaController(level.getCurArena(), level);
        ac.closeGrates();
        testAC.closeGrates();
        assertEquals(ac.getArena().getGratePositions(), testAC.getArena().getGratePositions());
        assertFalse(ac.getGrates().isEmpty());
    }

    @Test
    void openGrates() {
        ac.closeGrates();
        assertFalse(ac.getGrates().isEmpty());
        ac.openGrates();
        assertTrue(ac.getGrates().isEmpty());
        assertEquals(0, ac.getArena().getGrates().size());
    }

    @Test
    void arenaEnd() {
        Arena arena = ac.getArena();

        PlayerProfile profile;
        try {
            profile = PlayerProfile.loadFromJson();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        level.getSpawner().spawnHero(arena.getCenterWorldX(), arena.getCenterWorldY(), profile);

        // kill all mobs
        int totalSpawns = level.getDifficulty().getTotalSpawns();
        for (int i = 0; i < totalSpawns; i++) {
            for (Mob mob : arena.getActiveMobs()) {
                mob.die();
            }
            ac.update(level.getDifficulty().getTimePeriod());
        }
        // kill the miniboss
        for (Mob mob : arena.getActiveMobs()) {
            mob.die();
        }

        assertEquals(ArenaState.CLEARED, arena.getState());
        assertTrue(arena.isMiniBossDead());
        assertTrue(arena.getGrates().isEmpty());
    }
}
