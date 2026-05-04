package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanAttackMelee;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;

import java.util.List;

public abstract class MeleeMob extends Mob implements CanAttackMelee {
    public MeleeMob(int id, int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int multiplicator) {
        super(id, hp, speed, positionX, positionY, damage, reach, gold, faction, level, multiplicator);
    }
    protected double attackAngle(){
        return Math.PI/4;
    }
    protected static final double COOLDOWN_TIME = 1;  // to be balanced


    @Override
    public boolean isInRange(Entity entity) {
        if (entity == null){ return false; }
        double dx = entity.getX() - this.getX();
        double dy = entity.getY() - this.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= this.getReach();
    }

    @Override
    public boolean isInAngle(Entity entity, double curX, double curY) {
        double ang1 = Math.atan2(curY - this.getY(), curX - this.getX());
        double ang2 = Math.atan2(entity.getY() - this.getY(), entity.getX() - this.getX());
        double diff = Math.abs(ang1 - ang2);
        if (diff > Math.PI){
            diff = Math.abs(2 * Math.PI - diff);
        }
        return diff <= attackAngle();
    }

    @Override
    public void attack() {
        List<Entity> entitiesAround = getLevel().getEntitiesAround(this, getReach());
        for (Entity e : entitiesAround) {
            if (this.isEnemy(e)  && isInAngle(e, this.getLookX(), this.getLookY())) {
                e.takeDamage(getDamage(), this);
            }
        }
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        if (source != null) {
            setLookTarget(source.getX(), source.getY());
        }
    }

    @Override
    public void update(double deltaTime) {
       if(!this.isAlive()){return;}

       Hero target = this.findNearestPlayer();
       if (target == null){ return;}

       this.setLookTarget(target.getX(), target.getY());

       if (this.isInRange(target)) {
           if (attackCooldown <= 0) {
               this.attack();
               attackCooldown = COOLDOWN_TIME;
           }
       }  else {
           this.checkMoveToward(target, deltaTime);
       }

       if (attackCooldown > 0){
           attackCooldown -= deltaTime;
       }
    }
}


