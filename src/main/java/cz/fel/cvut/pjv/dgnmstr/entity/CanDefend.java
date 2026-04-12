package cz.fel.cvut.pjv.dgnmstr.entity;

/**
 * entity must be able to defend
 */
public interface CanDefend {
    /**
     * checks if  is enough defense to take less damage
     * @param damage amount to be dealt
     * @return true is / false is not
     */
    public boolean defenceCheck(int damage);
}
