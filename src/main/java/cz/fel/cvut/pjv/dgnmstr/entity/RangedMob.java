package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;
import java.util.Random;

public abstract class RangedMob extends Mob{

    protected Entity target = null;
    protected Random random = new Random();

    public RangedMob(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int multiplicator, int id) {
        super(hp, speed, positionX, positionY, damage, reach, gold, faction, level, multiplicator, id);
    }

    @Override
    public void attack() {
        if (target == null) {
            List<Entity> ents = getLevel().getEntitiesAround(this, getReach());
            if (!ents.isEmpty()) {
                target = ents.get(random.nextInt(ents.size()));
            } else {
                return;
            }
        }
        double deltaX = getX() - target.getX();
        double deltaY = getY() - target.getY();
        fireProjectile(deltaX, deltaY);


    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        target = source;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    protected abstract void fireProjectile(double deltaX, double deltaY);
}


