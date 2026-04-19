package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.List;
import java.util.Random;

public abstract class RangedMob extends Mob{

    protected Entity target = null;
    protected Random random = new Random();

    protected static final double COOLDOWN_TIME = 2.0;
    protected static final double DANGEROUS_DISTANCE = 0.25;

    public RangedMob(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int multiplicator) {
        super(hp, speed, positionX, positionY, damage, reach, gold, faction, level, multiplicator);
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
    protected void retreatFrom(Entity target,  double deltaTime) {
        if (target == null || !this.isAlive()) {return;}

        double dx = getX() - target.getX();
        double dy = getY() - target.getY();
        double dist =  Math.sqrt(dx * dx + dy * dy);

        if (dist < 0.1) {return;}

        double moveX = (dx / dist) * getSpeed() * deltaTime;
        double moveY = (dy / dist)  * getSpeed() * deltaTime;

        double newX = getX() + moveX;
        double newY = getY() + moveY;

        if (getLevel().isWalkable(newX, newY)) {
            setPos(newX, newY);
        } else if (getLevel().isWalkable(newX, getY())) {
            setPos(newX, getY());
        } else if (getLevel().isWalkable(getX(), newY)) {
            setPos(getX(), newY);
        }
    }
}


