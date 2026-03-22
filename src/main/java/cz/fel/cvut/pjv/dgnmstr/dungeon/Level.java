package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.StreamSupport.stream;

public class Level {
    public List<Entity> allEntities = new ArrayList<>();
    public List<Entity> getEntitiesAround(Entity center, double range) {
        double rangeSquared = range * range;
        return allEntities.stream()
                                .filter(other -> other != center) // 1. Исключаем самого себя
                                .filter(other -> calculateDistanceSquared(center, other) <= rangeSquared) // 2. Проверяем дистанцию
                                .toList(); // 3. Собираем в список (Java 16+)
    }

    private double calculateDistanceSquared(Entity a, Entity b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
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
}
