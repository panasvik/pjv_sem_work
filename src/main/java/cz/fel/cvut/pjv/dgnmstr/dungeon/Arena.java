package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaType;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TilePosition;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TileType;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;

import java.util.ArrayList;
import java.util.List;

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
    private final List<Grate> grates = new ArrayList<>();
    private boolean miniBossSpawned = false;
    private boolean miniBossDead = false;



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
    }

    public List<TilePosition> getGratePositions() {
        List<TilePosition> positions = new ArrayList<>();
        int corridorStartY = (startY + (height / 2) - 3);
        int corridorEndY = (startY + (height / 2) + 3);

        for (int y = corridorStartY; y <= corridorEndY; y++) {
            positions.add(new TilePosition(startX, y));
            positions.add(new TilePosition(startX + width - 1, y));
        }
        return positions;
    }

    public List<TilePosition> getValidSpawnPoints() {
        List<TilePosition> validSpawnPoints = new ArrayList<>();
        for (int x = startX; x < startX + width; x++) {
            for (int y = startY; y < startY + height; y++) {
                if (level.getTile(x, y) == TileType.SPAWN_FLOOR) {
                    validSpawnPoints.add(new TilePosition(x, y));
                }
            }
        }
        return validSpawnPoints;
    }

    /**
     * Checks if a specific set of world coordinates falls within this arena's grid bounds.
     * @return true if the position is inside the arena.
     */
    public boolean isInsideArena(double playerWorldX, double playerWorldY) {
        int playerGridX = (int) (playerWorldX / Level.TILE_SIZE);
        int playerGridY = (int) (playerWorldY / Level.TILE_SIZE);

        return playerGridX >= this.startX && playerGridX < (this.startX + this.width) &&
                playerGridY >= this.startY && playerGridY < (this.startY + this.height);
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

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArenaType getType() {
        return type;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public List<Mob> getActiveMobs() {
        return activeMobs;
    }

    public boolean isMiniBossSpawned() {
        return miniBossSpawned;
    }

    public boolean isMiniBossDead() {
        return miniBossDead;
    }

    public void setMiniBossSpawned(boolean miniBossSpawned) {
        this.miniBossSpawned = miniBossSpawned;
    }

    public void setMiniBossDead(boolean miniBossDead) {
        this.miniBossDead = miniBossDead;
    }

    public List<Grate> getGrates() {
        return grates;
    }

    public void addGrate(Grate grate){
        grates.add(grate);
    }

    public void clearGrate(){
        grates.clear();
    }

}