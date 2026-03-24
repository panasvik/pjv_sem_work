package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Fence;
import cz.fel.cvut.pjv.dgnmstr.entity.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.Hero;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.StreamSupport.stream;

public class Level {
    private final List<Entity> allEntities;
    private final List<Hero> players;
    private final List<Arena> arenas;

    public int playersAlive;

    private TileType[][] map;
    private int width;
    private int height;


    public Level() {
        this.allEntities = new ArrayList<>();
        this.players = new ArrayList<>();
        this.arenas = new ArrayList<>();
    }

    public void setMap(TileType[][] map, int width, int height) {
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public void addEntity(Entity entity) {
        this.allEntities.add(entity);
    }

    public void addPlayer(Hero player) {
        this.players.add(player);
        this.allEntities.add(player); // Игрок - это тоже Entity, он нужен для физики!
        this.playersAlive = players.size();
    }

    public void addArena(Arena arena) {
        this.arenas.add(arena);
    }



    public List<Entity> getEntitiesAround(Entity center, double range) {
        double rangeSquared = range * range;
        return allEntities.stream()
                                .filter(other -> other != center)
                                .filter(other -> calculateDistanceSquared(center, other) <= rangeSquared)
                                .toList();
    }

    private double calculateDistanceSquared(Entity a, Entity b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }

    public void playerDies() {
        playersAlive--;
        if (playersAlive == 0) this.gameOver();
    }

    public void gameOver() {

    }

    public void update() {
        allEntities.removeIf(entity -> !entity.isAlive());
        for (Arena arena: arenas) {
            arena.update(players);
        }

        for (Entity entity : allEntities) {
            // entity.update(); // Раскомментируй, когда добавишь update() в Entity
        }
    }

    // Проверка, можно ли физически встать на заданные координаты
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

    public double[] findFreeSpot(double startX, double startY, double rectWidth, double rectHeight) {
        int maxAttempts = 50;
        for (int i = 0; i < maxAttempts; i++) {
            double randomX = startX + (Math.random() * rectWidth);
            double randomY = startY + (Math.random() * rectHeight);

            if (isWalkable(randomX, randomY)) {
                return new double[]{randomX, randomY};
            }
        }

        return new double[]{startX + rectWidth / 2, startY + rectHeight / 2};
    }
}
