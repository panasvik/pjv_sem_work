package cz.fel.cvut.pjv.dgnmstr.view.anim;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Client-side animation state machine of one entity.
 * <p>
 * The server only sends positions, hp and an attack counter, so the animator
 * infers what the entity is doing by comparing consecutive snapshots:
 * movement means WALK, a new attack means ATTACK, lost hp means HURT and
 * zero hp or removal means DEATH. Switching states cross-fades the old frame
 * into the new one and gives the sprite a short squash so the change reads
 * as a transition rather than a hard cut.
 */
public class EntityAnimator {
    private static final double BLEND_TIME = 0.14;
    private static final double MOVE_GRACE = 0.14;
    private static final double MOVE_EPSILON = 0.05;
    private static final double FLASH_TIME = 0.14;
    private static final double COMBO_WINDOW = 0.9;
    private static final double HURT_FALLBACK_TIME = 0.3;
    private static final double HURT_RETRIGGER = 0.25;
    private static final double SPAWN_TIME = 0.55;
    private static final double CORPSE_LINGER = 0.7;
    private static final double CORPSE_FADE = 0.8;
    private static final double SQUASH_DECAY = 9.0;

    private static final ColorAdjust FLASH = new ColorAdjust(0, -0.6, 0.75, 0);

    private final SpriteSet sprites;
    private final boolean fadeInOnSpawn;

    private AnimState state = AnimState.IDLE;
    private double stateTime;
    private Facing facing = Facing.DOWN;
    private Facing side = Facing.RIGHT;
    private int variant;

    private Animation prevAnimation;
    private int prevFrame;
    private boolean prevMirrored;
    private double blend;

    private boolean initialised;
    private double lastX;
    private double lastY;
    private int lastHp;
    private int lastAttackCount;

    private double moveGrace;
    private double flash;
    private double sinceAttack = COMBO_WINDOW;
    private double age;
    private double squashX;
    private double squashY;

    /**
     * What changed on the observed entity since the previous frame.
     * @param hpDelta hp difference (negative for damage)
     * @param attacked whether a new attack started
     */
    public record Changes(int hpDelta, boolean attacked) {
        static final Changes NONE = new Changes(0, false);
    }

    /**
     * Creates a new EntityAnimator.
     * @param sprites the animations to use
     * @param fadeInOnSpawn whether the entity materialises instead of popping in
     */
    public EntityAnimator(SpriteSet sprites, boolean fadeInOnSpawn) {
        this.sprites = sprites;
        this.fadeInOnSpawn = fadeInOnSpawn;
        this.age = fadeInOnSpawn ? 0 : SPAWN_TIME;
    }

    /**
     * Advances timers and derives the next animation state from the entity.
     * @param entity the observed entity
     * @param dt seconds since the last frame
     * @return what changed since the last call
     */
    public Changes observe(Entity entity, double dt) {
        tick(dt);
        if (!initialised) {
            lastX = entity.getX();
            lastY = entity.getY();
            lastHp = entity.getHp();
            lastAttackCount = entity.getAttackCount();
            initialised = true;
            return Changes.NONE;
        }

        double dx = entity.getX() - lastX;
        double dy = entity.getY() - lastY;
        int hpDelta = entity.getHp() - lastHp;
        boolean attacked = entity.getAttackCount() != lastAttackCount;
        lastX = entity.getX();
        lastY = entity.getY();
        lastHp = entity.getHp();
        lastAttackCount = entity.getAttackCount();

        if (state == AnimState.DEATH) {
            return new Changes(hpDelta, false);
        }

        if (dx * dx + dy * dy > MOVE_EPSILON) {
            moveGrace = MOVE_GRACE;
            if (state != AnimState.ATTACK) {
                face(dx, dy);
            }
        }

        if (entity.getHp() <= 0) {
            startDeath();
            return new Changes(hpDelta, false);
        }

        if (attacked) {
            startAttack(entity);
        } else if (hpDelta < 0) {
            startHurt();
        }

        if (!state.isLooping() && stateTime >= stateDuration()) {
            switchTo(locomotion());
        }
        if (state.isLooping() && state != locomotion()) {
            switchTo(locomotion());
        }
        return new Changes(hpDelta, attacked);
    }

    /**
     * Advances the timers of an entity that is no longer observed (a corpse).
     * @param dt seconds since the last frame
     */
    public void tick(double dt) {
        age += dt;
        stateTime += dt;
        blend = Math.max(0, blend - dt);
        flash = Math.max(0, flash - dt);
        moveGrace -= dt;
        sinceAttack += dt;
        double decay = Math.exp(-SQUASH_DECAY * dt);
        squashX *= decay;
        squashY *= decay;
    }

    private void startAttack(Entity entity) {
        double lookDx = entity.getLookX() - entity.getX();
        double lookDy = entity.getLookY() - entity.getY();
        if (lookDx * lookDx + lookDy * lookDy > 1) {
            face(lookDx, lookDy);
        }
        int variants = Math.max(1, sprites.variantCount(AnimState.ATTACK, facing));
        variant = sinceAttack < COMBO_WINDOW ? (variant + 1) % variants : 0;
        sinceAttack = 0;
        switchTo(AnimState.ATTACK);
        squashX = 0.14;
        squashY = -0.10;
    }

    private void startHurt() {
        flash = FLASH_TIME;
        squashX = -0.10;
        squashY = 0.10;
        if (state == AnimState.ATTACK) {
            return;
        }
        if (state == AnimState.HURT && stateTime < HURT_RETRIGGER) {
            return;
        }
        switchTo(AnimState.HURT);
    }

    /**
     * Starts the death animation (used when hp hits zero or the entity disappears).
     */
    public void startDeath() {
        if (state == AnimState.DEATH) {
            return;
        }
        flash = FLASH_TIME;
        switchTo(AnimState.DEATH);
    }

    private AnimState locomotion() {
        return moveGrace > 0 ? AnimState.WALK : AnimState.IDLE;
    }

    private void face(double dx, double dy) {
        facing = Facing.fromVector(dx, dy);
        if (Math.abs(dx) > 0.01) {
            side = dx < 0 ? Facing.LEFT : Facing.RIGHT;
        }
    }

    private void switchTo(AnimState next) {
        Animation current = currentAnimation();
        prevAnimation = current;
        prevFrame = current.frameAt(stateTime, state.isLooping());
        prevMirrored = sprites.isMirrored(side);
        blend = BLEND_TIME;
        state = next;
        stateTime = 0;
    }

    private Animation currentAnimation() {
        return sprites.get(state, facing, state == AnimState.ATTACK ? variant : 0);
    }

    private double stateDuration() {
        if (!sprites.has(state, facing)) {
            return state == AnimState.ATTACK ? 0.35 : HURT_FALLBACK_TIME;
        }
        return currentAnimation().duration();
    }

    /**
     * Whether a corpse has finished playing and fading out.
     */
    public boolean isFinished() {
        return state == AnimState.DEATH && stateTime > deathLength() + CORPSE_LINGER + CORPSE_FADE;
    }

    private double deathLength() {
        return sprites.has(AnimState.DEATH, facing) ? currentAnimation().duration() : 0.6;
    }

    private double opacity() {
        double alpha = Math.min(1, age / SPAWN_TIME);
        if (state == AnimState.DEATH) {
            double fadeStart = deathLength() + CORPSE_LINGER;
            if (stateTime > fadeStart) {
                alpha *= Math.max(0, 1 - (stateTime - fadeStart) / CORPSE_FADE);
            }
            if (!sprites.has(AnimState.DEATH, facing)) {
                alpha *= Math.max(0, 1 - stateTime / deathLength());
            }
        }
        return alpha;
    }

    /**
     * Spawn progress from 0 (just appeared) to 1 (fully materialised).
     */
    public double spawnProgress() {
        return fadeInOnSpawn ? Math.min(1, age / SPAWN_TIME) : 1;
    }

    public AnimState getState() {
        return state;
    }

    public Facing getFacing() {
        return facing;
    }

    public SpriteSet getSprites() {
        return sprites;
    }

    private double hover() {
        return sprites.isFloating() ? Math.sin(age * 3.2) * 4 : 0;
    }

    /**
     * Draws the ground shadow under the sprite.
     */
    public void drawShadow(GraphicsContext gc, double screenX, double screenY) {
        double width = sprites.getWidth() * 0.32 * sprites.getShadowScale();
        Animation anim = currentAnimation();
        double height = sprites.getWidth() * anim.frameHeight() / anim.frameWidth();
        double footY = screenY + height * sprites.getFootOffset();
        double lift = sprites.isFloating() ? (hover() + 4) / 16 : 0;
        double shrink = 1 - 0.15 * lift;
        gc.setGlobalAlpha(0.45 * opacity() * (1 - 0.3 * lift));
        gc.setFill(Color.BLACK);
        gc.fillOval(screenX - width * shrink / 2, footY - width * shrink * 0.18, width * shrink, width * shrink * 0.36);
        gc.setGlobalAlpha(1);
    }

    /**
     * Draws the current frame, cross-faded with the previous one during a transition.
     * @param gc graphics context
     * @param screenX centre x on screen
     * @param screenY centre y on screen
     */
    public void draw(GraphicsContext gc, double screenX, double screenY) {
        double alpha = opacity();
        if (alpha <= 0) {
            return;
        }
        double spawn = spawnProgress();
        double rise = (1 - spawn) * 10;
        double scaleX = (1 + squashX) * (0.7 + 0.3 * spawn);
        double scaleY = (1 + squashY) * (0.7 + 0.3 * spawn) + (1 - spawn) * 0.3;
        if (state == AnimState.DEATH && !sprites.has(AnimState.DEATH, facing)) {
            double shrink = Math.max(0.2, 1 - stateTime / deathLength() * 0.6);
            scaleX *= shrink;
            scaleY *= shrink;
        }
        double y = screenY + hover() + rise;
        boolean mirrored = sprites.isMirrored(side);

        Animation anim = currentAnimation();
        int frame = anim.frameAt(stateTime, state.isLooping());
        gc.save();
        if (flash > 0) {
            gc.setEffect(FLASH);
        }
        drawFrame(gc, anim, frame, mirrored, screenX, y, scaleX, scaleY, alpha);
        if (blend > 0 && prevAnimation != null) {
            drawFrame(gc, prevAnimation, prevFrame, prevMirrored, screenX, y, scaleX, scaleY, alpha * blend / BLEND_TIME);
        }
        gc.restore();
    }

    private void drawFrame(GraphicsContext gc, Animation anim, int frame, boolean mirrored,
                           double x, double y, double scaleX, double scaleY, double alpha) {
        Image image = anim.image();
        if (image == null) {
            return;
        }
        double width = sprites.getWidth();
        double height = width * anim.frameHeight() / anim.frameWidth();
        gc.save();
        gc.translate(x, y);
        gc.scale(mirrored ? -scaleX : scaleX, scaleY);
        gc.setGlobalAlpha(alpha);
        gc.drawImage(image, anim.sourceX(frame), anim.sourceY(frame), anim.frameWidth(), anim.frameHeight(),
                -width / 2, -height / 2, width, height);
        gc.restore();
    }
}
