package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static cz.fel.cvut.pjv.dgnmstr.dungeon.LevelState.*;
import static java.util.stream.StreamSupport.stream;

public class Level {
    private final List<Entity> allEntities;
    private final List<Hero> players;
    private final List<Arena> arenas;
    private final List<Projectile> projectiles;

    private final Map<Integer, Entity> entityMap;
    private int nextEntityId = 0;

    private int playersAlive;

    private TileType[][] map;
    private int width;
    private int height;
    private LevelState state;
    private final DifficultyStats difficulty;
    public static final int TILE_SIZE = 32;

    private static final Logger log = Logger.getLogger(Level.class.getName());

    private List<BossSpawnRate> bossSpawnRate;
    private Boss levelBoss;
    private boolean bossSpawned = false;
    private boolean bossDead = false;

    public Level(DifficultyStats difficulty) {
        this.allEntities = new ArrayList<>();
        this.players = new ArrayList<>();
        this.arenas = new ArrayList<>();
        this.state = NOT_STARTED;
        this.difficulty = difficulty;
        this.projectiles = new ArrayList<>();
        this.entityMap = new HashMap<>();
        this.bossSpawnRate = new ArrayList<>();
        this.bossSpawnRate.add(new BossSpawnRate(BossStats.ANYSEK_THE_BEHEMOTH, 0.5));
        this.bossSpawnRate.add(new BossSpawnRate(BossStats.DWIGHT_THE_LEVIATHAN, 0.5));
    }

    public int getNewID() {
        return nextEntityId++;
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


    public void startLevel() {
        this.state = LevelState.PLAYING;
        this.playersAlive = players.size();
        log.info("Level started");
    }

    public void setMap(TileType[][] map, int width, int height) {
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public void addPlayer(PlayerStats stats) {
        Hero player = new Warrior(stats, 64, 64, this);
        this.players.add(player);
        this.addEntity(player);
        log.info("Player added to the level.");
        this.playersAlive = players.size();
    }

    public void addArena(Arena arena) {
        this.arenas.add(arena);
    }

    public List<Entity> getEntitiesAround(Entity center, double range) {
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
        projectiles.add(p);
    }
    public void update(double deltaTime) {
        if (state != LevelState.PLAYING) {
            return;
        }
        if (!arenas.isEmpty() && !bossSpawned) {
            Arena lastArena = arenas.getLast();
            if(lastArena.getState() == ArenaState.CLEARED){
                spawnBoss();
            }
        }
        if (bossSpawned && !bossDead && levelBoss != null) {
            if(!levelBoss.isAlive()){
                bossDead = true;
                log.info("The final boss " + levelBoss.getClass().getSimpleName() + " is dead.");
            }
        }
        for( Arena arena : arenas){
            arena.update(players, deltaTime);
        }
        for (Entity e : new ArrayList<>(allEntities)) {
            if (e.isAlive()) {
                e.update(deltaTime);
            }
        }
        for (Projectile p : new ArrayList<>(projectiles)) {
            p.update(deltaTime);
        }
        allEntities.removeIf(e -> !e.isAlive() && !(e instanceof Hero) );
        checkExit();
    }

    public void playerDies(Hero deadPlayer) {
        deadPlayer.die();
        playersAlive--;
        log.info("Player dead, active players:" + playersAlive);

        if (playersAlive <= 0) {
            gameOver();
        } else {
            removeEntity(deadPlayer);
        }
    }

    private void gameOver() {
        this.state = LevelState.GAME_OVER;
        log.info("GAME OVER!");
        // will send info to a controller that the lvl is over
    }

    private void levelComplete() {
        this.state = LevelState.VICTORY;
        log.info("VICTORY!");
        // will send info to a controller that the lvl is complete
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

    private boolean isAtExit(double worldX, double worldY) {
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

    public Arena getCurrentArena() {
        for (Arena arena : arenas) {
            if (arena.getState() != ArenaState.CLEARED) return arena;
        }
        return arenas.getLast();
    }
    private void spawnBoss() {
        Arena lastArena = arenas.getLast();

        double random = Math.random();
        double sum = 0;
        BossStats selectedBoss = BossStats.ANYSEK_THE_BEHEMOTH;
        for (BossSpawnRate rate : bossSpawnRate) {
            sum += rate.chance();
            if (random <= sum) {
                selectedBoss = rate.type();
                break;
            }
        }

        double spawnX = lastArena.getCenterWorldX();
        double spawnY = lastArena.getCenterWorldY();

        if (selectedBoss == BossStats.ANYSEK_THE_BEHEMOTH) {
            levelBoss = new AnysekTheBehemoth(spawnX, spawnY, this);
        } else {
            levelBoss = new DwightTheLeviathan(spawnX, spawnY, this);
        }
        addEntity(levelBoss);
        bossSpawned = true;

        log.info("FINAL BOSS AWAITS! " + levelBoss.getClass().getSimpleName() + " spawned in the center.");

    }

    private void checkExit(){
        if (bossDead){
            for (Hero player : players) {
                if (player.isAlive() && isAtExit(player.getX(), player.getY())) {
                    levelComplete();
                    break;
                }
            }
        }
    }

}
