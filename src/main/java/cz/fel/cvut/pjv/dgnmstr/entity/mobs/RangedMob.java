package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.List;
import java.util.Random;

public abstract class RangedMob extends Mob{

    protected Hero target = null;


    protected static final double COOLDOWN_TIME = 2.0;
    protected static final double DANGEROUS_DISTANCE = 0.5;

    public RangedMob(int id, int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int multiplicator) {
        super(id, hp, speed, positionX, positionY, damage, reach, gold, faction, level, multiplicator);
    }

    @Override
    public void attack() {

        double deltaX = target.getCenterX() - getCenterX();
        double deltaY = target.getCenterY() - getCenterY();
        fireProjectile(deltaX, deltaY);


    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
       if(source instanceof Hero hero){
           target = hero;
       }

    }


    protected abstract void fireProjectile(double deltaX, double deltaY);

    @Override
    public void update(double deltaTime) {
        if (!this.isAlive()) {return;}

        if (target == null || !target.isAlive()) {
            target = this.findNearestPlayer();
        }

        if (target == null ) {return;}

        this.setLookTarget(target.getCenterX(), target.getCenterY());

        double dist = MyMath.calculateDistanceSquared(this, target);

        double maxReach = getReach();
        double sqReach = getReach() * (getReach());
        double dangerousDist = maxReach * DANGEROUS_DISTANCE;
        double sqDangerousDist = dangerousDist * dangerousDist;

        if (dist > sqReach) {
            this.checkMoveToward(target, deltaTime);
        } else if (dist < sqDangerousDist) {
            this.retreatFrom(target, deltaTime);
        }

        if (attackCooldown <= 0 && dist <= sqReach){
            this.attack();
            attackCooldown = COOLDOWN_TIME;
        }

        if (attackCooldown > 0){
            attackCooldown -= deltaTime;
        }
    }
}


