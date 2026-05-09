package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.DifficultyStats;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DungeonGeneratorTest {
    DungeonGenerator generatorClient;
    DungeonGenerator generatorServer;
    Random random = new Random();

    @BeforeEach
    void setUp() {
        generatorClient = new DungeonGenerator();
        generatorServer = new DungeonGenerator();
    }

    @Test
    void generate() {
        long seed = random.nextLong();
        Level levelClient = generatorClient.generate(DifficultyStats.MEDIUM, seed);
        Level levelServer = generatorServer.generate(DifficultyStats.MEDIUM, seed);
        assertEquals(levelClient, levelServer, "Levels with the same seed must be equal");

    }
}