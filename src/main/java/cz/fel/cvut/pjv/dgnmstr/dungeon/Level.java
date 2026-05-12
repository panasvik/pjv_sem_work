package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.*;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.Boss;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.BossStats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Positionable;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.Projectile;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.ProjectileType;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState.*;
import static java.lang.Math.abs;

public class Level {
    private EntitySpawner spawner;
    public static final int MAX_ARENA_IDX = 3;
    private final List<Entity> allEntities;
    private final List<Hero> players;
    private final List<Arena> arenas;
    private Arena curArena;
    private final List<Projectile> projectiles;
    List<Chest> chests;
    private final Map<Integer, Entity> entityMap;
    private final Map<Integer, Projectile> projectileMap;

    private int playersAlive;

    private TileType[][] map;
    private int width;
    private int height;
    private LevelState state;
    private final DifficultyStats difficulty;
    public static final int TILE_SIZE = 32;

    private static final Logger log = Logger.getLogger(Level.class.getName());

    private final List<BossSpawnRate> bossSpawnRate;
    private Boss levelBoss;
    private boolean bossSpawned = false;

    public Level(DifficultyStats difficulty) {
        this.allEntities = new ArrayList<>();
        this.players = new ArrayList<>();
        this.arenas = new ArrayList<>();
        this.state = NOT_STARTED;
        this.difficulty = difficulty;
        this.projectiles = new ArrayList<>();
        this.entityMap = new ConcurrentHashMap<>();
        this.projectileMap = new ConcurrentHashMap<>();
        this.bossSpawnRate = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.bossSpawnRate.add(new BossSpawnRate(BossStats.ANYSEK_THE_BEHEMOTH, 0.5));
        this.bossSpawnRate.add(new BossSpawnRate(BossStats.DWIGHT_THE_LEVIATHAN, 0.5));
    }



    public Entity getEntity(int id) {
        return entityMap.get(id);
    }


    public void addEntity(Entity entity) {
        if (entity != null && !entityMap.containsKey(entity.getId())) {
            allEntities.add(entity);
            entityMap.put(entity.getId(), entity);
        }
    }

    public void setMap(TileType[][] map, int width, int height) {
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public void addPlayer(Hero player) {
        this.players.add(player);
        this.addEntity(player);
        log.info("Player added to the level.");
        this.playersAlive = players.size();
    }


    public void addArena(Arena arena) {
        this.arenas.add(arena);
    }

    public <T extends Positionable> List<Entity> getEntitiesAround(T center, double range) {
        double rangeSquared = range * range;
        return allEntities.stream()
                            .filter(other -> other != center)
                            .filter(other -> MyMath.calculateDistanceSquared(center, other) <= rangeSquared)
                            .toList();
    }



    public void removeEntity(Entity entity) {
        if (entity != null) {
            allEntities.remove(entity);
            entityMap.remove(entity.getId());        }
    }

    public LevelState getState() {
        return state;
    }

    public DifficultyStats getDifficulty() {
        return difficulty;
    }

    public void addProjectile(Projectile p) {
        if (p != null && !projectileMap.containsKey(p.getId())) {
            projectiles.add(p);
            projectileMap.put(p.getId(), p);
        }

    }

    public boolean isWalkable(double targetWorldX, double targetWorldY, double half) {
        return isWalkableTile(targetWorldX, targetWorldY, half) && isWalkableEntity(targetWorldX, targetWorldY, half);
    }

    public boolean isWalkableTile(double targetWorldX, double targetWorldY, double half) {
        double targetMinX = targetWorldX - half;
        double targetMaxX = targetWorldX + half;
        double targetMinY = targetWorldY - half;
        double targetMaxY = targetWorldY + half;

        if (targetMinX < 0 || targetMinY < 0 ||
                targetMaxX > width * TILE_SIZE || targetMaxY > height * TILE_SIZE) {
            return false;
        }
        // Axis-Aligned Bounding Box (AABB) collision detects overlaps between
        // two non-rotated rectangles by checking if their edges intersect along both X
        // and Y axes
        int minGridX = (int) (targetMinX / TILE_SIZE);
        int maxGridX = getMaxTileIndex(targetMaxX);
        int minGridY = (int) (targetMinY / TILE_SIZE);
        int maxGridY = getMaxTileIndex(targetMaxY);

        for (int x = minGridX; x <= maxGridX; x++) {
            for (int y = minGridY; y <= maxGridY; y++) {
                if (map[x][y] != TileType.WALL) {
                    continue;
                }

                double tileMinX = x * TILE_SIZE;
                double tileMaxX = tileMinX + TILE_SIZE;
                double tileMinY = y * TILE_SIZE;
                double tileMaxY = tileMinY + TILE_SIZE;

                if (intersectsAabb(targetMinX, targetMaxX, targetMinY, targetMaxY,
                        tileMinX, tileMaxX, tileMinY, tileMaxY)) {
                    return false;
                }
            }
        }

        return true;
    }

    private int getMaxTileIndex(double maxWorldCoordinate) {
        if (maxWorldCoordinate <= 0) {
            return 0;
        }
        return (int) ((maxWorldCoordinate - Double.MIN_VALUE) / TILE_SIZE);
    }

    private boolean intersectsAabb(double firstMinX, double firstMaxX, double firstMinY, double firstMaxY,
                                   double secondMinX, double secondMaxX, double secondMinY, double secondMaxY) {
        return firstMinX < secondMaxX && firstMaxX > secondMinX &&
                firstMinY < secondMaxY && firstMaxY > secondMinY;
    }

    public boolean isWalkableEntity(double targetWorldX, double targetWorldY, double half) {
        for (Entity e : allEntities) {
            if (!(e.isAlive() && e.isSolid())) {
                continue;
            }
            if (abs(targetWorldX - e.getX()) < half + e.getHalfHitBox() &&
                    abs(targetWorldY - e.getY()) < half + e.getHalfHitBox()) {
                return false;
            }
        }
        return true;
    }

    public <T extends Positionable> List<Entity> getCollisions(T target, double half) {
        List<Entity> collisions = new ArrayList<>();
        for (Entity e : allEntities) {
            if (!(e.isAlive() && e.isSolid())) {
                continue;
            }
            if (abs(target.getX() - e.getX()) < half + e.getHalfHitBox() &&
                    abs(target.getY() - e.getY()) < half + e.getHalfHitBox()) {
                collisions.add(e);
            }
        }
        return collisions;
    }


    public boolean isAtExit(double worldX, double worldY) {
        int gridX = (int) (worldX / TILE_SIZE);
        int gridY = (int) (worldY / TILE_SIZE);

        if (gridX < 0 || gridY < 0 || gridX >= width || gridY >= height) {
            return false;
        }

        return map[gridX][gridY] == TileType.EXIT;
    }

    public TileType getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return TileType.WALL;
        }
        return map[x][y];
    }


    public int getPlayersAlive() {
        return playersAlive;
    }



    public void removeProjectile(Projectile p) {
        if (p == null) return;
        projectiles.remove(p);
    }

    public List<Hero> getPlayers() {
        return players;
    }

    public void setCurArena(Arena curArena) {
        this.curArena = curArena;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public List<Entity> getAllEntities() {
        return allEntities;
    }

    public Arena getCurArena() {
        return curArena;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public Map<Integer, Entity> getEntityMap() {
        return entityMap;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<BossSpawnRate> getBossSpawnRate() {
        return bossSpawnRate;
    }

    public Boss getLevelBoss() {
        return levelBoss;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }


    public void setPlayersAlive(int playersAlive) {
        this.playersAlive = playersAlive;
    }

    public void setState(LevelState state) {
        this.state = state;
    }


    public void setBossSpawned(boolean bossSpawned) {
        this.bossSpawned = bossSpawned;
    }

    public void setLevelBoss(Boss levelBoss) {
        this.levelBoss = levelBoss;
    }

    public void setSpawner(EntitySpawner spawner) {
        this.spawner = spawner;
    }

    public EntitySpawner getSpawner() {
        return spawner;
    }

    public void requestProjectileSpawn(ProjectileType type, Entity master, int radius, int damage, double deltaX, double deltaY, double velocity) {
        if (spawner != null) {
            spawner.spawnProjectile(type, master, radius, damage, deltaX, deltaY, velocity);
        }
    }

    public void requestGoldSpawn(double x, double y, int amount) {
        if (spawner != null) {
            spawner.spawnGold(x, y, amount);
        }
    }

    public Mob requestMobSpawn(double x, double y, MobStats stats, int mult) {
        if (spawner != null) {
            return spawner.spawnMob(x, y, stats, mult);
        }
        return null;
    }

    public Boss requestBossSpawn(double x, double y, BossStats stats) {
        if (spawner != null) {
            return spawner.spawnBoss(x, y, stats);
        }
        return null;
    }

    public Grate requestGrateSpawn(double x, double y) {
        if (spawner != null) {
            return spawner.spawnGrate(x, y);
        }
        return null;
    }

    public void requestChestSpawn(double x, double y, Inventory inventory) {
        if (spawner != null) {
            spawner.spawnChest(x, y, inventory);
        }
    }

    public void requestFenceSpawn(double x, double y) {
        if (spawner != null) {
            spawner.spawnFence(x, y);
        }
    }

    public Hero requestHeroSpawn(double x, double y, PlayerProfile profile) {
        if (spawner != null) {
            return spawner.spawnHero(x, y, profile);
        }
        return null;
    }

    public Map<Integer, Projectile> getProjectileMap() {
        return projectileMap;
    }




    public void addChest(Chest chest) {
        chests.add(chest);
    }

    public List<Chest> getChests() {
        return chests;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Level level = (Level) o;
        return getPlayersAlive() == level.getPlayersAlive() && getWidth() == level.getWidth() && getHeight() == level.getHeight() && isBossSpawned() == level.isBossSpawned() && Objects.equals(spawner, level.spawner) && Objects.equals(getAllEntities(), level.getAllEntities()) && Objects.equals(getPlayers(), level.getPlayers()) && Objects.equals(getArenas(), level.getArenas()) && Objects.equals(getCurArena(), level.getCurArena()) && Objects.equals(getProjectiles(), level.getProjectiles()) && Objects.equals(getChests(), level.getChests()) && Objects.equals(getEntityMap(), level.getEntityMap()) && Objects.equals(getProjectileMap(), level.getProjectileMap()) && Objects.deepEquals(map, level.map) && getState() == level.getState() && getDifficulty() == level.getDifficulty() && Objects.equals(getBossSpawnRate(), level.getBossSpawnRate()) && Objects.equals(getLevelBoss(), level.getLevelBoss());
    }

    @Override
    public int hashCode() {
        return Objects.hash(spawner, getAllEntities(), getPlayers(), getArenas(), getCurArena(), getProjectiles(), getChests(), getEntityMap(), getProjectileMap(), getPlayersAlive(), Arrays.deepHashCode(map), getWidth(), getHeight(), getState(), getDifficulty(), getBossSpawnRate(), getLevelBoss(), isBossSpawned());
    }
}
