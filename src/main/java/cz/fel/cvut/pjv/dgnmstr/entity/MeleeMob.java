package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;

public abstract class MeleeMob extends Mob implements CanAttackMelee{
    public MeleeMob(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level) {
        super(hp, speed, positionX, positionY, damage, reach, gold, faction, level);
    }
    protected double attackAngle(){
        return Math.PI/4;
    }

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
}


