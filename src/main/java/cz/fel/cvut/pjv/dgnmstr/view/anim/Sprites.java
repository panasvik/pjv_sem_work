package cz.fel.cvut.pjv.dgnmstr.view.anim;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;

import java.util.EnumMap;
import java.util.Map;

import static cz.fel.cvut.pjv.dgnmstr.view.anim.AnimState.*;

/**
 * Catalogue describing how every creature sprite sheet is cut into animations.
 */
public final class Sprites {
    private static final String MOBS = "/cz/fel/cvut/pjv/dgnmstr/entity/mobs/";
    private static final String BOSS = "/cz/fel/cvut/pjv/dgnmstr/entity/boss/";
    private static final String HERO = "/cz/fel/cvut/pjv/dgnmstr/entity/hero/";

    private static final Map<ImgId, SpriteSet> cache = new EnumMap<>(ImgId.class);

    private Sprites() {
    }

    /**
     * Sprite set for the given entity image, or null for static objects.
     * @param id image id of the entity
     * @return animations of the entity or null
     */
    public static synchronized SpriteSet forImg(ImgId id) {
        if (id == null) {
            return null;
        }
        if (!cache.containsKey(id)) {
            cache.put(id, create(id));
        }
        return cache.get(id);
    }

    private static double width(ImgId id) {
        return Level.TILE_SIZE * id.getScale();
    }

    private static SpriteSet create(ImgId id) {
        return switch (id) {
            case WARRIOR -> warrior(id);
            case GOBLIN -> goblinSheet(MOBS + "GoblinScout-Sheet.png", 6, 14);
            case SKELETON -> goblinSheet(MOBS + "GoblinArcher-Sheet.png", 9, 16);
            case ZOMBIE -> zombie(id);
            case GHOST -> ghost(id);
            case EVIL_WIZARD -> wizard(id);
            case ANYSEK -> behemoth(id);
            case DWIGHT -> leviathan(id);
            default -> null;
        };
    }

    private static SpriteSet warrior(ImgId id) {
        SpriteSet set = new SpriteSet(width(id)).shadow(0.8, 0.2);
        for (Facing facing : Facing.values()) {
            String dir = switch (facing) {
                case UP -> "Up";
                case DOWN -> "Down";
                case LEFT -> "Left";
                case RIGHT -> "Right";
            };
            String base = HERO + dir + "/Png/Warrior" + dir;
            set.add(facing, IDLE, Animation.strip(base + "Idle.png", 48, 48, 7));
            set.add(facing, WALK, Animation.strip(base + "Walk.png", 48, 48, 12));
            set.add(facing, ATTACK, Animation.strip(base + "Attack01.png", 48, 48, 22));
            if (facing != Facing.DOWN) {
                set.add(facing, ATTACK, Animation.strip(base + "Attack02.png", 48, 48, 22));
                set.add(facing, ATTACK, Animation.strip(base + "Attack03.png", 48, 48, 22));
            }
            set.add(facing, HURT, Animation.strip(base + "Hurt.png", 48, 48, 14));
            set.add(facing, DEATH, Animation.strip(base + "Death.png", 48, 48, 7));
        }
        return set;
    }

    /** Goblin scout and goblin archer share one layout: idle, attack, hurt, walk, death rows. */
    private static SpriteSet goblinSheet(String sheet, int attackFrames, double attackFps) {
        return new SpriteSet(Level.TILE_SIZE * 2.4).facing(Facing.RIGHT).shadow(0.9, 0.38)
                .add(IDLE, Animation.row(sheet, 600, 500, 0, 0, 8, 8))
                .add(ATTACK, Animation.row(sheet, 600, 500, 1, 0, attackFrames, attackFps))
                .add(HURT, Animation.row(sheet, 600, 500, 2, 0, 4, 12))
                .add(WALK, Animation.row(sheet, 600, 500, 3, 0, 6, 10))
                .add(DEATH, Animation.row(sheet, 600, 500, 4, 0, 9, 12));
    }

    private static SpriteSet zombie(ImgId id) {
        String sheet = MOBS + "Zombie.png";
        return new SpriteSet(width(id)).facing(Facing.RIGHT).shadow(0.8, 0.4)
                .add(IDLE, Animation.row(sheet, 32, 32, 0, 0, 8, 8))
                .add(ATTACK, Animation.row(sheet, 32, 32, 1, 0, 7, 14))
                .add(WALK, Animation.row(sheet, 32, 32, 2, 0, 8, 9))
                .add(HURT, Animation.row(sheet, 32, 32, 5, 0, 2, 10))
                .add(DEATH, Animation.row(sheet, 32, 32, 5, 1, 7, 11));
    }

    private static SpriteSet ghost(ImgId id) {
        String sheet = MOBS + "Ghost.png";
        return new SpriteSet(width(id)).floating().shadow(0.6, 0.45)
                .add(IDLE, Animation.column(sheet, 32, 32, 2, 0, 8, 8))
                .add(ATTACK, Animation.row(sheet, 32, 32, 4, 0, 2, 5))
                .add(DEATH, Animation.column(sheet, 32, 32, 3, 0, 8, 12));
    }

    private static SpriteSet wizard(ImgId id) {
        return new SpriteSet(width(id)).facing(Facing.RIGHT).shadow(0.8, 0.2)
                .add(IDLE, Animation.strip(MOBS + "WizardIdle.png", 150, 150, 8))
                .add(WALK, Animation.strip(MOBS + "WizardMove.png", 150, 150, 10))
                .add(ATTACK, Animation.strip(MOBS + "WizardAttack.png", 150, 150, 14))
                .add(HURT, Animation.strip(MOBS + "WizardTakeHit.png", 150, 150, 12))
                .add(DEATH, Animation.strip(MOBS + "WizardDeath.png", 150, 150, 8));
    }

    private static SpriteSet behemoth(ImgId id) {
        String sheet = BOSS + "Behemoth.png";
        return new SpriteSet(width(id)).shadow(1.6, 0.3)
                .add(IDLE, Animation.row(sheet, 128, 128, 0, 0, 6, 7))
                .add(Facing.LEFT, WALK, Animation.row(sheet, 128, 128, 1, 0, 6, 9))
                .add(Facing.UP, WALK, Animation.row(sheet, 128, 128, 1, 0, 6, 9))
                .add(Facing.RIGHT, WALK, Animation.row(sheet, 128, 128, 2, 0, 6, 9))
                .add(Facing.DOWN, WALK, Animation.row(sheet, 128, 128, 2, 0, 6, 9))
                .add(ATTACK, Animation.row(sheet, 128, 128, 3, 0, 6, 12))
                .add(HURT, Animation.row(sheet, 128, 128, 4, 0, 6, 14))
                .add(DEATH, Animation.row(sheet, 128, 128, 5, 0, 6, 6));
    }

    private static SpriteSet leviathan(ImgId id) {
        return new SpriteSet(width(id)).facing(Facing.LEFT).floating().shadow(1.8, 0.42)
                .add(IDLE, Animation.row(BOSS + "leviathan.png", 612, 648, 0, 0, 1, 1));
    }
}
