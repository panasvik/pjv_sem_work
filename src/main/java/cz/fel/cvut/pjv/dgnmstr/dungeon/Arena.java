package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.GameApp;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Arena {
    private ArenaState state = ArenaState.NOT_STARTED;

    private final int startX;
    private final int startY;
    private final int width = 64;
    private final int height = 64;

    private final Level level;
    private final ArenaType type;
    private final List<Mob> activeMobs = new ArrayList<>();
    private final List<Entity> grates = new ArrayList<>(); // Решетки на входах

    private static final Logger log =  Logger.getLogger(Arena.class.getName());


    public Arena(int startX, int startY, Level level, ArenaType type) {
        this.startX = startX;
        this.startY = startY;
        this.level = level;
        this.type = type;
        spawnChests();
    }

//    public void update(List<Hero> players) {
//        switch (state) {
//            case NOT_STARTED -> checkPlayerEntry(players);
//            case WAVE_FIGHT  -> checkWaveProgress();
//            case MINI_BOSS_FIGHT  -> checkBossProgress();
//            case CLEARED     -> {  }
//        }
//    }

    private void checkPlayerEntry(List<Hero> players) {
        Hero firstPlayerToEnter = null;

        for (Hero player : players) {
            if (player.isAlive() && isInsideArena(player.getX(), player.getY())) {
                firstPlayerToEnter = player;
                break;
            }
        }

        if (firstPlayerToEnter != null) {
            log.info("Player entered arena. Starting WAVE_FIGHT.");
            this.state = ArenaState.WAVE_FIGHT;
            closeGrates();
            teleportOtherPlayers(firstPlayerToEnter, players);
            spawnInitialMobs();
        }
    }

    private void checkWaveProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty() && state == ArenaState.WAVE_FIGHT) {
            log.info("Spawning Miniboss");
            spawnMiniBoss();
            this.state = ArenaState.MINI_BOSS_FIGHT;
        }
    }

    private void checkBossProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty()) {
            log.info("Arena finished");
            openGrates();
            this.state = ArenaState.CLEARED;
        }
    }


    private void closeGrates() {
        int corridorStartY = (startY + (height / 2) - 3);
        int corridorEndY = (startY + (height / 2) + 3);

        for (int y = corridorStartY; y <= corridorEndY; y++) {
            double leftWorldX = (startX * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
            double rightWorldX = ((startX + width - 1) * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
            double worldY = (y * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
            Grate leftGrate = new Grate(leftWorldX, worldY, level);
            Grate rightGrate = new Grate(rightWorldX, worldY, level);
            grates.add(leftGrate);
            grates.add(rightGrate);
            level.addEntity(leftGrate);
            level.addEntity(rightGrate);
        }
    }
    private void openGrates() {
        for (Entity grate : grates) {
            grate.die();
        }
        grates.clear();
    }

    private void teleportOtherPlayers(Hero firstPlayer, List<Hero> players) {
        for (Hero player : players) {
            if (player != firstPlayer && player.isAlive()) {
                player.setPos( firstPlayer.getX() + Level.TILE_SIZE, firstPlayer.getY());
            }
        }
    }


    private void spawnMobs(int cap, List<TilePosition> validSpawnPoints) {
        int mobsToSpawn = Math.min(cap, validSpawnPoints.size());

        for (int i = 0; i < mobsToSpawn; i++) {
            int randomIndex = (int) (Math.random() * validSpawnPoints.size());
            TilePosition spot = validSpawnPoints.remove(randomIndex);

            double worldX = (spot.x() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
            double worldY = (spot.y() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);

            Mob mob = type.spawnRandomMob(worldX, worldY, level);

            activeMobs.add(mob);
            level.addEntity(mob);
        }
    }

    private void spawnInitialMobs() {
        List<TilePosition> validSpawnPoints = new ArrayList<>();

        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                if (level.getTile(x, y) == TileType.SPAWN_FLOOR) {
                    validSpawnPoints.add(new TilePosition(x, y));
                }
            }
        }

        if (validSpawnPoints.isEmpty()) {
            log.severe("No spawn points available, aborting");
            return;
        }

        spawnMobs(level.getDifficulty().getInitCap(), validSpawnPoints);
    }

    private void spawnMiniBoss() {
        double centerWorldX = (startX + width / 2.0) * Level.TILE_SIZE;
        double centerWorldY = (startY + height / 2.0) * Level.TILE_SIZE;

        Mob boss = type.spawnMiniBoss(centerWorldX, centerWorldY, level);

        activeMobs.add(boss);
        level.addEntity(boss);

        log.info("MiniBoss " + boss.getClass().getSimpleName() + " spawned!");
    }


    private boolean isInsideArena(double playerWorldX, double playerWorldY) {
        int playerGridX = (int) (playerWorldX / Level.TILE_SIZE);
        int playerGridY = (int) (playerWorldY / Level.TILE_SIZE);

        return playerGridX >= this.startX && playerGridX < (this.startX + this.width) &&
                playerGridY >= this.startY && playerGridY < (this.startY + this.height);
    }

    private void spawnChests() {
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {

                if (level.getTile(x, y) == TileType.CHEST_FLOOR) {
                    double worldX = (x * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
                    double worldY = (y * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);

                    Chest chest = new Chest(worldX, worldY, level, new Inventory());
                    level.addEntity(chest);
                }
            }
        }
    }


    public ArenaState getState() {
        return state;
    }
}