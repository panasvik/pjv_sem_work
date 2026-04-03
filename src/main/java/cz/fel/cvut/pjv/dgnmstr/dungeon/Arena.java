package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.Chest;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private ArenaState state = ArenaState.NOT_STARTED;

    private final int startX;
    private final int startY;
    private final int width = 64;
    private final int height = 64;

    private final Level level;

    private final List<Mob> activeMobs = new ArrayList<>();
    private final List<Entity> grates = new ArrayList<>(); // Решетки на входах

    public Arena(int startX, int startY, Level level) {
        this.startX = startX;
        this.startY = startY;
        this.level = level;
        spawnChests();
    }

    public void update(List<Hero> players) {
        switch (state) {
            case NOT_STARTED -> checkPlayerEntry(players);
            case WAVE_FIGHT  -> checkWaveProgress();
            case MINI_BOSS_FIGHT  -> checkBossProgress();
            case CLEARED     -> {  }
        }
    }

    private void checkPlayerEntry(List<Hero> players) {
        Hero firstPlayerToEnter = null;

        for (Hero player : players) {
            if (player.isAlive() && isInsideArena(player.getX(), player.getY())) {
                firstPlayerToEnter = player;
                break;
            }
        }

        if (firstPlayerToEnter != null) {
            this.state = ArenaState.WAVE_FIGHT;
            closeGrates();
            teleportOtherPlayers(firstPlayerToEnter, players);
            spawnInitialMobs();
        }
    }

    private void checkWaveProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty() && state == ArenaState.WAVE_FIGHT) {
            System.out.println("Spawning Miniboss");
            spawnMiniBoss();
            this.state = ArenaState.MINI_BOSS_FIGHT;
        }
    }

    private void checkBossProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty()) {
            System.out.println("Arena finished");
            openGrates();
            this.state = ArenaState.CLEARED;
        }
    }


    private void closeGrates() {

        int corridorStartY = (startY + (height / 2) - 3);
        int corridorEndY = (startY + (height / 2) + 3);

        for (int y = corridorStartY; y <= corridorEndY; y++) {
            Grate leftGrate = new Grate(startX, y, level);
            Grate rightGrate = new Grate(startX + width - 1, y, level);
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
            TilePosition spot = validSpawnPoints.get(randomIndex);
            validSpawnPoints.remove(randomIndex);
            double worldX = (spot.x() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
            double worldY = (spot.y() * Level.TILE_SIZE) + (Level.TILE_SIZE / 2.0);
            // Mob mob = new Zombie(...);
            // activeMobs.add(mob);
            // level.addEntity(mob);
        }
    }

    private void spawnInitialMobs() {
        List<TilePosition> validSpawnPoints = new ArrayList<>();

        for (int x =  startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                if (level.getTile(x, y) == TileType.SPAWN_FLOOR) {
                    validSpawnPoints.add(new TilePosition(x, y));
                }
            }
        }

        if (validSpawnPoints.isEmpty()) {
            System.err.println("No SPAWN_FLOOR (*) in file. Mobs will not spawn!");
            return;
        }

        spawnMobs(level.getDifficulty().getInitCap(), validSpawnPoints);
    }

    private void spawnMiniBoss() {
        //TODO
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