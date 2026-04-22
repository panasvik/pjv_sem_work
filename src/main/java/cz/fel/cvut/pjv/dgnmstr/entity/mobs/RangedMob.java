package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.List;
import java.util.Random;

public abstract class RangedMob extends Mob{

    protected Entity target = null;
    protected Random random = new Random();

    protected static final double COOLDOWN_TIME = 2.0;
    protected static final double DANGEROUS_DISTANCE = 0.25;

    public RangedMob(int id, int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int multiplicator) {
        super(id, hp, speed, positionX, positionY, damage, reach, gold, faction, level, multiplicator);
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
        double deltaX = target.getX() - getX();
        double deltaY = target.getY() - getY();
        fireProjectile(deltaX, deltaY);


    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        target = source;
    }


    protected abstract void fireProjectile(double deltaX, double deltaY);

    @Override
    public void update(double deltaTime) {
        if (!this.isAlive()) {return;}

        if (target == null || !target.isAlive()) {
            target = this.findNearestPlayer();
        }

        if (target == null ) {return;}

        this.setLookTarget(target.getX(), target.getY());

        double distSquared = MyMath.calculateDistanceSquared(this, target);
        double dist = Math.sqrt(distSquared);

        double maxReach = getReach();
        double dangerousDist = maxReach * DANGEROUS_DISTANCE;

        if (dist > maxReach) {
            this.checkMoveToward(target, deltaTime);
        } else if (dist < dangerousDist) {
            this.retreatFrom(target, deltaTime);
        }

        if (attackCooldown <= 0 && dist <= maxReach){
            this.attack();
            attackCooldown = COOLDOWN_TIME;
        }

        if (attackCooldown > 0){
            attackCooldown -= deltaTime;
        }
    }
}


