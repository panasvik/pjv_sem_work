package cz.fel.cvut.pjv.dgnmstr.dungeon.controllers;

import cz.fel.cvut.pjv.dgnmstr.dungeon.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.DifficultyStats;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TilePosition;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Updatable;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArenaController implements Updatable {
    private double spawnTime = 0;
    private final double spawnDelay;
    private Arena arena;
    private Level level;

    private int totalSpawnedInWave = 0;
    private int currentMobCount = 0;

    private record MobSpawnData(MobStats mobStats, double x, double y){}

    private static final Logger log = Logger.getLogger(ArenaController.class.getName());


    public ArenaController(Arena arena, Level level) {
        this.arena = arena;
        this.level = level;
        this.spawnDelay = level.getDifficulty().getTimePeriod();
    }



    @Override
    public void update(double deltaTime) {
        currentMobCount = arena.getActiveMobs().size();
        if (arena.getState() == ArenaState.NOT_STARTED) {
            checkPlayerEntry(level.getPlayers());
        } else if (arena.getState() == ArenaState.WAVE_FIGHT) {
            checkWaveProgress(deltaTime);
        } else if (arena.getState() == ArenaState.MINI_BOSS_FIGHT) {
            checkMiniBossProgress();
        }
    }

    public void closeGrates() {
        List<TilePosition> gratePos = arena.getGratePositions();

        for (TilePosition pos : gratePos) {
            double worldX = (pos.x() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
            double worldY = (pos.y() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);

            Grate grate = getLevel().requestGrateSpawn(worldX, worldY);
            if (grate != null) {
                arena.addGrate(grate);
            }
        }
    }



    private void checkPlayerEntry(List<Hero> players) {
        Hero firstPlayerToEnter = null;

        for (Hero player : players) {
            if (player.isAlive() && arena.isInsideArena(player.getCenterX(), player.getCenterY())) {
                firstPlayerToEnter = player;
                break;
            }
        }

        if (firstPlayerToEnter != null) {
            log.info("Player entered arena. Starting WAVE_FIGHT.");
            arena.setState(ArenaState.WAVE_FIGHT);
            closeGrates();
            teleportOtherPlayers(firstPlayerToEnter, players);
            spawnInitialMobs();
        }
    }

    /**
     * Monitors the remaining mobs in the current wave.
     * Transitions to the miniboss phase once all regular enemies are defeated.
     */
    private void checkWaveProgress(double dTime) {
        arena.getActiveMobs().removeIf(mob -> !mob.isAlive());

        if (totalSpawnedInWave < level.getDifficulty().getTotalSpawns()
            && currentMobCount < level.getDifficulty().getMobCap()) {
            spawnTime += dTime;
            if (spawnTime >= spawnDelay) {
                spawnPeriodicMobs();
                // subtracting instead of nullifying to damp lags
                spawnTime -= spawnDelay;
            }
        }
        if (checkMiniBossSpawnCond()) {
            log.info("Spawning Miniboss");
            spawnMiniBoss();
            arena.setState(ArenaState.MINI_BOSS_FIGHT);
        }
    }

    private boolean checkMiniBossSpawnCond() {
        return arena.getActiveMobs().isEmpty() &&
                level.getDifficulty().getTotalSpawns() <= totalSpawnedInWave &&
                arena.getState() == ArenaState.WAVE_FIGHT;
    }

    /**
     * Once all mobs defeated, unlocks the arena exits and sets state to CLEARED.
     */
    private void checkMiniBossProgress() {
        arena.getActiveMobs().removeIf(mob -> !mob.isAlive());

        if (arena.getActiveMobs().isEmpty()) {
            log.info("Arena finished");
            openGrates();
            arena.setMiniBossDead(true);
            arena.setState(ArenaState.CLEARED);
        }
    }
    public void openGrates() {
        for (Entity grate : arena.getGrates()) {
            grate.die();
        }
        arena.clearGrate();
    }

    private void teleportOtherPlayers(Hero firstPlayer, List<Hero> players) {
        for (Hero player : players) {
            if (player != firstPlayer && player.isAlive()) {
                player.setPos( firstPlayer.getCenterX() + Level.TILE_SIZE, firstPlayer.getCenterY());
            }
        }
    }


    private MobSpawnData getMobSpawnData(List<TilePosition> validSpawnPoints) {
        int randomIndex = (int) (Math.random() * validSpawnPoints.size());
        TilePosition spot = validSpawnPoints.remove(randomIndex);

        double worldX = (spot.x() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
        double worldY = (spot.y() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);

        MobStats mobStats = arena.getType().getRandomMobStats();

        return new MobSpawnData(mobStats, worldX, worldY);
    }

    protected void spawnMobs(int cap, List<TilePosition> validSpawnPoints) {
        int mobsToSpawn = Math.min(cap, validSpawnPoints.size());
        for (int i = 0; i < mobsToSpawn; i++) {
            MobSpawnData mobSpawnData = getMobSpawnData(validSpawnPoints);
            Mob mob = level.requestMobSpawn( mobSpawnData.x, mobSpawnData.y, mobSpawnData.mobStats,1);
            if (mob != null) {
                arena.getActiveMobs().add(mob);
            }
        }
    }

    private void spawnPeriodicMobs() {
        int remainingToSpawn = level.getDifficulty().getTotalSpawns() - totalSpawnedInWave;
        int mobsToSpawn = Math.min(level.getDifficulty().getSpawnRate(), remainingToSpawn);

        if (mobsToSpawn > 0) {
            List<TilePosition> validSpawnPoints = arena.getValidSpawnPoints();
            spawnMobs(mobsToSpawn, validSpawnPoints);
            totalSpawnedInWave += mobsToSpawn;
        }
    }


    protected void spawnInitialMobs() {
        List<TilePosition> validSpawnPoints = arena.getValidSpawnPoints();

        if (validSpawnPoints.isEmpty()) {
            log.warning("No spawn points available");
            return;
        }

        int initialCap = level.getDifficulty().getInitCap();

        spawnMobs(initialCap, validSpawnPoints);
        totalSpawnedInWave += initialCap;
    }

    private void spawnMiniBoss() {
        List<TilePosition> validSpawnPoints = arena.getValidSpawnPoints();
        MobSpawnData mobSpawnData = getMobSpawnData(validSpawnPoints);
        Mob miniboss = level.requestMobSpawn( mobSpawnData.x, mobSpawnData.y, mobSpawnData.mobStats,DifficultyStats.MEDIUM.getStatsMult());
        if (miniboss != null) {
            arena.getActiveMobs().add(miniboss);
            arena.setMiniBossSpawned(true);
            log.info("MiniBoss " + miniboss.getClass().getSimpleName() + " spawned!");
        } else {
            log.warning("Miniboss spawn failed!");
        }
    }



    public Arena getArena() {
        return arena;
    }

    public Level getLevel() {
        return level;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Grate> getGrates() {
        return arena.getGrates();
    }
}
