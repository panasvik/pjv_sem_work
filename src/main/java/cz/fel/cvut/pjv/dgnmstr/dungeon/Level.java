package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.Projectile;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.ArrayList;
import java.util.List;

import static cz.fel.cvut.pjv.dgnmstr.dungeon.LevelState.NOT_STARTED;
import static java.util.stream.StreamSupport.stream;

public class Level {
    public List<Entity> allEntities = new ArrayList<>();
    private LevelState state;
    private final DifficultyStats difficulty;
    private List<Hero> players;
    private List<Projectile> projectiles;

    public Level(DifficultyStats difficulty) {
        this.allEntities = new ArrayList<>();
        this.players = new ArrayList<>();
        this.state = NOT_STARTED;
        this.difficulty = difficulty;
        this.projectiles = new ArrayList<>();

    }


    public List<Entity> getEntitiesAround(Entity center, double range) {
        double rangeSquared = range * range;
        return allEntities.stream()
                                .filter(other -> other != center) // 1. Исключаем самого себя
                                .filter(other -> MyMath.calculateDistanceSquared(center, other) <= rangeSquared) // 2. Проверяем дистанцию
                                .toList(); // 3. Собираем в список (Java 16+)
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
}
