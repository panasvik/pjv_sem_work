package cz.fel.cvut.pjv.dgnmstr.view.anim;

/**
 * Direction a sprite is facing.
 */
public enum Facing {
    UP, DOWN, LEFT, RIGHT;

    /**
     * Picks the dominant direction of a vector.
     * @param dx x component
     * @param dy y component
     * @return facing along the stronger axis
     */
    public static Facing fromVector(double dx, double dy) {
        if (Math.abs(dx) >= Math.abs(dy)) {
            return dx < 0 ? LEFT : RIGHT;
        }
        return dy < 0 ? UP : DOWN;
    }
}
