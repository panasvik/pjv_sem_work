package cz.fel.cvut.pjv.dgnmstr.entity;

public interface CanAttackMelee extends CanAttack{
    public boolean isInRange(Entity entity);
    public boolean isInAngle(Entity entity, double curX, double curY);
}
