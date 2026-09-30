package cz.fel.cvut.pjv.dgnmstr.view.anim;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * All animations of one kind of creature, together with how it is drawn.
 * Animations can be shared by every facing or overridden per facing
 * (the hero has separate sheets for each direction).
 */
public final class SpriteSet {
    private final Map<AnimState, List<Animation>> shared = new EnumMap<>(AnimState.class);
    private final Map<Facing, Map<AnimState, List<Animation>>> perFacing = new EnumMap<>(Facing.class);
    private final double width;
    private Facing nativeFacing;
    private boolean floating;
    private double shadowScale = 1.0;
    private double footOffset = 0.35;

    /**
     * Creates a new SpriteSet.
     * @param width on-screen width of one frame in pixels
     */
    public SpriteSet(double width) {
        this.width = width;
    }

    /**
     * Adds an animation used for every facing. Adding more for the same
     * state creates variants (e.g. an attack combo).
     */
    public SpriteSet add(AnimState state, Animation animation) {
        shared.computeIfAbsent(state, s -> new ArrayList<>()).add(animation);
        return this;
    }

    /**
     * Adds an animation used only for the given facing.
     */
    public SpriteSet add(Facing facing, AnimState state, Animation animation) {
        perFacing.computeIfAbsent(facing, f -> new EnumMap<>(AnimState.class))
                .computeIfAbsent(state, s -> new ArrayList<>()).add(animation);
        return this;
    }

    /**
     * Declares which way the art faces, so it is mirrored for the other side.
     */
    public SpriteSet facing(Facing nativeFacing) {
        this.nativeFacing = nativeFacing;
        return this;
    }

    /**
     * Makes the sprite hover up and down.
     */
    public SpriteSet floating() {
        this.floating = true;
        return this;
    }

    /**
     * Tunes the ground shadow.
     * @param scale shadow size relative to the default
     * @param footOffset where the feet are, as a fraction of the drawn height below the centre
     */
    public SpriteSet shadow(double scale, double footOffset) {
        this.shadowScale = scale;
        this.footOffset = footOffset;
        return this;
    }

    private List<Animation> variants(AnimState state, Facing facing) {
        Map<AnimState, List<Animation>> own = perFacing.get(facing);
        if (own != null && own.containsKey(state)) {
            return own.get(state);
        }
        return shared.get(state);
    }

    public boolean has(AnimState state, Facing facing) {
        return variants(state, facing) != null;
    }

    public int variantCount(AnimState state, Facing facing) {
        List<Animation> list = variants(state, facing);
        return list == null ? 0 : list.size();
    }

    /**
     * Animation for the state, falling back to idle when the state has no art.
     */
    public Animation get(AnimState state, Facing facing, int variant) {
        List<Animation> list = variants(state, facing);
        if (list == null) {
            list = variants(AnimState.IDLE, facing);
        }
        return list.get(Math.floorMod(variant, list.size()));
    }

    /**
     * Whether the art must be flipped horizontally for this side.
     */
    public boolean isMirrored(Facing side) {
        if (nativeFacing == null) {
            return false;
        }
        return side != nativeFacing;
    }

    public double getWidth() {
        return width;
    }

    public boolean isFloating() {
        return floating;
    }

    public double getShadowScale() {
        return shadowScale;
    }

    public double getFootOffset() {
        return footOffset;
    }
}
