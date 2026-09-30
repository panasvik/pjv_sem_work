package cz.fel.cvut.pjv.dgnmstr.view.dungeon;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaType;
import javafx.scene.paint.Color;

import java.util.List;

import static cz.fel.cvut.pjv.dgnmstr.view.dungeon.Decor.*;

/**
 * Visual theme of a part of the dungeon: tint, typical decorations,
 * banner colour and ambient particles.
 */
public enum DungeonTheme {
    CORRIDOR("Corridor", null, -1, List.of(RUBBLE, PUDDLE, BONES, RUBBLE, BLOOD), null),
    GOBLIN_CAMP("Goblin Camp", Color.rgb(150, 85, 20, 0.10), 0, List.of(BONES, RUBBLE, BLOOD, COINS, SKULL, BONES), null),
    UNDEAD_CRYPT("Undead Crypt", Color.rgb(40, 120, 80, 0.11), 1, List.of(SKULL, BONES, MUSHROOMS, RUNE, CANDLES, BONES), Color.rgb(120, 255, 190)),
    SPIDER_NEST("Spider Nest", Color.rgb(100, 50, 140, 0.12), 2, List.of(WEB, BONES, SKULL, WEB, PUDDLE), null),
    FORGE("The Forge", Color.rgb(180, 50, 10, 0.13), 0, List.of(LAVA, RUBBLE, COINS, LAVA, RUBBLE), Color.rgb(255, 140, 40)),
    ROYAL_TOMB("Royal Tomb", Color.rgb(170, 140, 40, 0.10), 3, List.of(CANDLES, COINS, RUNE, SKULL, CANDLES), Color.rgb(255, 225, 140));

    private final String title;
    private final Color tint;
    private final int banner;
    private final List<Decor> decorations;
    private final Color ambient;

    DungeonTheme(String title, Color tint, int banner, List<Decor> decorations, Color ambient) {
        this.title = title;
        this.tint = tint;
        this.banner = banner;
        this.decorations = decorations;
        this.ambient = ambient;
    }

    /**
     * Theme belonging to an arena type.
     * @param type arena type, or null for corridors
     * @return matching theme
     */
    public static DungeonTheme of(ArenaType type) {
        if (type == null) {
            return CORRIDOR;
        }
        return switch (type) {
            case GOBLIN_CAMP -> GOBLIN_CAMP;
            case UNDEAD_CRYPT -> UNDEAD_CRYPT;
            case SPIDER_NEST -> SPIDER_NEST;
            case FORGE -> FORGE;
            case ROYAL_TOMB -> ROYAL_TOMB;
        };
    }

    public String getTitle() {
        return title;
    }

    public Color getTint() {
        return tint;
    }

    /**
     * Index of the banner colour in Banner.png, or -1 for none.
     */
    public int getBanner() {
        return banner;
    }

    public List<Decor> getDecorations() {
        return decorations;
    }

    /**
     * Colour of ambient floating particles, or null for none.
     */
    public Color getAmbient() {
        return ambient;
    }
}
