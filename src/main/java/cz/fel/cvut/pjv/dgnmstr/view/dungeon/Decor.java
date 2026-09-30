package cz.fel.cvut.pjv.dgnmstr.view.dungeon;

import javafx.scene.paint.Color;

/**
 * Floor decorations in Decor.png, in sheet order. Some of them glow.
 */
public enum Decor {
    BONES(null, 0),
    SKULL(null, 0),
    COBWEB(null, 0),
    RUBBLE(null, 0),
    BLOOD(null, 0),
    PUDDLE(null, 0),
    MUSHROOMS(Color.rgb(60, 220, 200, 0.22), 42),
    CANDLES(Color.rgb(255, 180, 80, 0.26), 58),
    LAVA(Color.rgb(255, 90, 30, 0.30), 48),
    RUNE(Color.rgb(150, 90, 255, 0.20), 44),
    WEB(null, 0),
    COINS(Color.rgb(255, 210, 90, 0.10), 24);

    private final Color light;
    private final double lightRadius;

    Decor(Color light, double lightRadius) {
        this.light = light;
        this.lightRadius = lightRadius;
    }

    public Color getLight() {
        return light;
    }

    public double getLightRadius() {
        return lightRadius;
    }

    public boolean glows() {
        return light != null;
    }
}
