package cz.fel.cvut.pjv.dgnmstr.entity;

/**
 * contractor must be able to attack with melee
 */
public interface CanAttackMelee extends CanAttack{
    /**
     * @param entity who to check for
     * @return true is / false is not
     */
    public boolean isInRange(Entity entity);

    /**
     * @param entity who to check for
     * @param curX cursor X position
     * @param curY cursor Y position
     * @return true is / false is not
     */
    public boolean isInAngle(Entity entity, double curX, double curY);
}
