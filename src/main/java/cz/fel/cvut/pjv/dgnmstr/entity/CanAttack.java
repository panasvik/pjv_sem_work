package cz.fel.cvut.pjv.dgnmstr.entity;

/**
 * contractor must be able to attack
 */
public interface CanAttack {
    public void attack();
    public int getDamage();
}
