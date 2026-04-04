package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Fence;
import cz.fel.cvut.pjv.dgnmstr.entity.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.Projectile;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.ArrayList;
import java.util.List;

import static cz.fel.cvut.pjv.dgnmstr.dungeon.LevelState.*;
import static java.util.stream.StreamSupport.stream;

public class Level {
    private final List<Entity> allEntities;
    private final List<Hero> players;
    private final List<Arena> arenas;
    private List<Projectile> projectiles;

    private int playersAlive;

    private TileType[][] map;
    private int width;
    private int height;
    private LevelState state;
    private final DifficultyStats difficulty;
    public static final int TILE_SIZE = 32;

    public Level(DifficultyStats difficulty) {
        this.allEntities = new ArrayList<>();
        this.players = new ArrayList<>();
        this.arenas = new ArrayList<>();
        this.state = NOT_STARTED;
        this.difficulty = difficulty;
        this.projectiles = new ArrayList<>();


    }

    public void startLevel() {
        this.state = LevelState.PLAYING;
        this.playersAlive = players.size();

        System.out.println("Level started");
    }

    public void setMap(TileType[][] map, int width, int height) {
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public void addPlayer(Hero player) {
        this.players.add(player);
        this.allEntities.add(player);
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

    public void  addEntity(Entity entity) {
        if(entity != null && !allEntities.contains(entity)) {
            allEntities.add(entity);
        }
    }
    public void  removeEntity(Entity entity) {
        if (entity != null) {
            allEntities.remove(entity);
        }
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
    public void update() {
        if (state != LevelState.PLAYING) {
            return;
        }

        allEntities.removeIf(entity -> !entity.isAlive());
        for (Arena arena: arenas) {
            arena.update(players);
        }

        for (Entity entity : allEntities) {
            entity.update();
        }
    }

    public boolean isWalkable(double targetX, double targetY) {
        int gridX = (int) targetX;
        int gridY = (int) targetY;
        if (gridX < 0 || gridY < 0 || gridX >= width || gridY >= height) {
            return false;
        }
        if (map[gridX][gridY] == TileType.WALL) {
            return false;
        }
        for (Entity e : allEntities) {
            if (e.isAlive()) {
                if ((int) e.getX() == gridX && (int) e.getY() == gridY) {

                    if (e instanceof Grate ||
                            e instanceof Fence) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void playerDies(Hero deadPlayer) {
        deadPlayer.die();
        playersAlive--;
        System.out.println("Player dead, active players:" + playersAlive);

        if (playersAlive <= 0) {
            gameOver();
        } else {
            allEntities.remove(deadPlayer);
        }
    }

    private void gameOver() {
        this.state = LevelState.GAME_OVER;
        System.out.println("GAME OVER!");
        // GameManager.getInstance().showGameOverScreen();
    }

    private void levelComplete() {
        this.state = LevelState.VICTORY;
        System.out.println("VICTORY!");
        // GameManager.getInstance().showVictoryScreen(players);
    }

    private boolean isAtExit(double x, double y) {
        int gridX = (int) x;
        int gridY = (int) y;

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
}
