package cz.fel.cvut.pjv.dgnmstr.view.anim;

/**
 * Animation states an animated entity can be in.
 * Looping states run until something else happens,
 * one-shot states play once and then hand control back.
 */
public enum AnimState {
    IDLE(true),
    WALK(true),
    ATTACK(false),
    HURT(false),
    DEATH(false);

    private final boolean looping;

    AnimState(boolean looping) {
        this.looping = looping;
    }

    public boolean isLooping() {
        return looping;
    }
}
