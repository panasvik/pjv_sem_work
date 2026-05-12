package cz.fel.cvut.pjv.dgnmstr.dungeon.controllers;

import cz.fel.cvut.pjv.dgnmstr.dungeon.DungeonGenerator;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.DifficultyStats;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.TestSpawner;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelControllerTest {
    LevelController lc;
    Level level;

    @BeforeEach
    void setUp() {
        DungeonGenerator generator = new DungeonGenerator();
        long testSeed = 12345678L;
        level = generator.generate(DifficultyStats.MEDIUM, testSeed);
        IDGenerator gen = new IDGenerator();
        TestSpawner testSpawner = new TestSpawner(level, gen);
        level.setSpawner(testSpawner);
        lc = new LevelController(level);
    }

    @Test
    void startLevel() {
        lc.startLevel();
        assertEquals(LevelState.PLAYING, lc.getLevel().getState());
    }

    @Test
    void updateEntities() {
        PlayerProfile profile = PlayerProfile.loadFromJson();
        Hero w1 = level.requestHeroSpawn(128, 128, profile);
        Inputs inputs = new Inputs(true, true, true, true, false,
                false, true, 500, 500);
        w1.setInputs(inputs);
        double heroX = w1.getX();
        double heroY = w1.getY();
        lc.startLevel();
        lc.update(1000);
        List<Entity> currentEntities = List.copyOf(lc.getArenaController().getArena().getActiveMobs());
        List<Double> entityXes = new ArrayList<>();
        List<Double> entityYes = new ArrayList<>();
        System.out.println(currentEntities.size());
        for (Entity e : level.getAllEntities()) {
            entityXes.add(e.getX());
            entityYes.add(e.getY());
        }
        lc.updateEntities(16.0);
        for (int i = 0; i < currentEntities.size(); i++) {
            assertNotEquals(entityXes.get(i), currentEntities.get(i).getX());
            assertNotEquals(entityYes.get(i), currentEntities.get(i).getY());
        }
        assertNotEquals(heroX, w1.getX());
        assertNotEquals(heroY, w1.getY());
    }


    @Test
    void playerDies() {
        PlayerProfile profile = PlayerProfile.loadFromJson();
        Hero w1 = level.requestHeroSpawn(128, 128, profile);
        lc.startLevel();
        int before = level.getPlayersAlive();
        lc.playerDies(w1);
        assertEquals(before - 1, level.getPlayersAlive());
        assertFalse(w1.isAlive());
        assertEquals(LevelState.GAME_OVER, level.getState());
    }

    @Test
    void checkExit() {
        PlayerProfile profile = PlayerProfile.loadFromJson();
        Hero w1 = level.requestHeroSpawn(128, 128, profile);
        lc.startLevel();

        boolean exitFound = false;
        for(int x = 0; x < level.getWidth(); x++) {
            for(int y = 0; y < level.getHeight(); y++) {
                if (level.getTile(x, y) == cz.fel.cvut.pjv.dgnmstr.dungeon.service.TileType.EXIT) {
                    double targetX = x * Level.TILE_SIZE + Level.TILE_SIZE / 2.0;
                    double targetY = y * Level.TILE_SIZE + Level.TILE_SIZE / 2.0;
                    w1.setPos(targetX, targetY);
                    exitFound = true;
                    break;
                }
            }
            if (exitFound) break;
        }

        lc.checkExit();
        assertEquals(LevelState.VICTORY, level.getState());
    }

    @Test
    void activateNextArena() {
        lc.startLevel();
        ArenaController firstArenaController = lc.getArenaController();
        lc.activateNextArena();
        assertNotEquals(firstArenaController, lc.getArenaController());
    }

    @Test
    void spawnChests() {
        lc.spawnChests();
        assertFalse(level.getChests().isEmpty());
    }

    @Test
    void spawnFences() {
        int entitiesBefore = level.getAllEntities().size();
        lc.spawnFences();
        int entitiesAfter = level.getAllEntities().size();
        assertTrue(entitiesAfter > entitiesBefore);
    }
}