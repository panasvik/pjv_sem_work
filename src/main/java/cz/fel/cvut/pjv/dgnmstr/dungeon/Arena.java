package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.GameApp;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a single enclosed room (Arena) within a dungeon level.
 * The arena acts as a state machine, managing the progression from the moment
 * a player enters, through waves of regular monsters, up to a final miniboss fight.
 * It also handles the locking and unlocking of entrances (grates) and loot spawning.
 */

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

    /**
     * Constructs a new Arena at the specified grid coordinates.
     * Automatically spawns chests within the arena boundaries upon creation.
     * @param startX The starting X coordinate (in grid tiles) of the arena.
     * @param startY The starting Y coordinate (in grid tiles) of the arena.
     * @param level  The parent Level instance this arena belongs to.
     * @param type   The thematic type of the arena, determining which mobs will spawn.
     */
    public Arena(int startX, int startY, Level level, ArenaType type) {
        this.startX = startX;
        this.startY = startY;
        this.level = level;
        this.type = type;
        spawnChests();
    }

    public void update(List<Hero> players, double deltaTime) {
        switch (state) {
            case NOT_STARTED:
                checkPlayerEntry(players);
                break;
            case WAVE_FIGHT:
                checkWaveProgress();
                break;
            case MINI_BOSS_FIGHT:
                checkBossProgress();
                break;
            case CLEARED:
                break;
        }
    }

    /**
     * Scans for any player entering the arena bounds.
     * If detected, locks the arena and starts the first wave.
     */
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

    /**
     * Monitors the remaining mobs in the current wave.
     * Transitions to the miniboss phase once all regular enemies are defeated.
     */
    private void checkWaveProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty() && state == ArenaState.WAVE_FIGHT) {
            log.info("Spawning Miniboss");
            spawnMiniBoss();
            this.state = ArenaState.MINI_BOSS_FIGHT;
        }
    }

    /**
     * Once all mobs defeated, unlocks the arena exits and sets state to CLEARED.
     */
    private void checkBossProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty()) {
            log.info("Arena finished");
            openGrates();
            this.state = ArenaState.CLEARED;
        }
    }

    /**
     * Spawns solid {@link Grate} entities at the arena's entrance and exit corridors
     * to trap everything inside during combat.
     */
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

    /**
     * Removes all {@link Grate} entities, allowing player to move to the next arena.
     */
    private void openGrates() {
        for (Entity grate : grates) {
            grate.die();
        }
        grates.clear();
    }
    /**
     * Ensures all players are pulled into the arena when combat starts,
     * preventing them from being stuck behind locked grates.
     */
    private void teleportOtherPlayers(Hero firstPlayer, List<Hero> players) {
        for (Hero player : players) {
            if (player != firstPlayer && player.isAlive()) {
                player.setPos( firstPlayer.getX() + Level.TILE_SIZE, firstPlayer.getY());
            }
        }
    }


    /**
     * Method that helps to spawn a specific number of enemies at random valid locations.
     * @param cap The maximum number of mobs to spawn.
     * @param validSpawnPoints Pre-filtered list of floor tiles suitable for spawning.
     */
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

    /**
     * Checks if a specific set of world coordinates falls within this arena's grid bounds.
     * @return true if the position is inside the arena.
     */
    private boolean isInsideArena(double playerWorldX, double playerWorldY) {
        int playerGridX = (int) (playerWorldX / Level.TILE_SIZE);
        int playerGridY = (int) (playerWorldY / Level.TILE_SIZE);

        return playerGridX >= this.startX && playerGridX < (this.startX + this.width) &&
                playerGridY >= this.startY && playerGridY < (this.startY + this.height);
    }

    /**
     * Iterates through the arena grid to find chest spawn locations and places interactable chests.
     */
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

    public double getCenterWorldX() {
        return (startX + width / 2.0) * Level.TILE_SIZE;
    }

    public double getCenterWorldY() {
        return (startY + height / 2.0) * Level.TILE_SIZE;
    }
}