package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.EntitySpawner;
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
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState.*;

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
                                .filter(other -> MyMath.calculateDistanceSquared(center, other) <= rangeSquared) // 2. Проверяем дистанцию
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



    public boolean isWalkable(double targetWorldX, double targetWorldY) {
        int gridX = (int) (targetWorldX / TILE_SIZE);
        int gridY = (int) (targetWorldY / TILE_SIZE);

        if (gridX < 0 || gridY < 0 || gridX >= width || gridY >= height) {
            return false;
        }
        if (map[gridX][gridY] == TileType.WALL) {
            return false;
        }
        for (Entity e : allEntities) {
            if (e.isAlive()) {
                int eGridX = (int) (e.getX() / TILE_SIZE);
                int eGridY = (int) (e.getY() / TILE_SIZE);

                if (eGridX == gridX && eGridY == gridY) {
                    if (e.isSolid()) {
                        return false;
                    }
                }
            }
        }
        return true;
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

    public void requestProjectileSpawn(Entity master, int radius, int damage, double deltaX, double deltaY, double velocity) {
        if (spawner != null) {
            spawner.spawnProjectile(master, radius, damage, deltaX, deltaY, velocity);
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

    public Chest requestChestSpawn(double x, double y, Inventory inventory) {
        if (spawner != null) {
            return spawner.spawnChest(x, y, inventory);
        }
        return null;
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


    public void spawnChests() {
        for (Arena a: arenas) {
            for (TilePosition p : a.getChestSpawnPoints()) {
                requestChestSpawn(p.x() * TILE_SIZE, p.y() * TILE_SIZE, Inventory.getRandomInventory());
            }
        }
    }

    public void addChest(Chest chest) {
        chests.add(chest);
    }

    public List<Chest> getChests() {
        return chests;
    }
}