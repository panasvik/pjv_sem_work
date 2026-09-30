package cz.fel.cvut.pjv.dgnmstr.view.dungeon;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Arena;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TileType;
import cz.fel.cvut.pjv.dgnmstr.global.ImgLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.List;

import static cz.fel.cvut.pjv.dgnmstr.dungeon.Level.TILE_SIZE;

/**
 * Draws the dungeon tile map with a 3/4 look: walls facing the camera show a
 * brick front, the rest of the wall mass is a dark top with a lit rim.
 * Floors get variants, decorations, wall shadows and a per-arena tint.
 * Decoration layout is derived from tile coordinates, so every client
 * sees the same dungeon without any extra network traffic.
 */
public class DungeonPainter {
    private static final String DIR = "/cz/fel/cvut/pjv/dgnmstr/entity/map/dungeon/";
    private static final int SRC = 16;
    private static final int FACE_CRACKED = 3;
    private static final int FACE_MOSSY = 4;
    private static final int FACE_CHAINS = 5;
    private static final Color RIM = Color.rgb(78, 70, 104);
    private static final Color TORCH_LIGHT = Color.rgb(255, 140, 50, 0.30);
    private static final Color EXIT_LIGHT = Color.rgb(170, 120, 255, 0.35);

    private final Level level;
    private final int width;
    private final int height;
    private final byte[][] floorVariant;
    private final byte[][] faceVariant;
    private final byte[][] decor;
    private final boolean[][] torch;
    private final boolean[][] banner;
    private final DungeonTheme[] columnTheme;
    private final List<Light> lights = new ArrayList<>();

    private final Image floor = ImgLoader.sheet(DIR + "Floor.png");
    private final Image wallFace = ImgLoader.sheet(DIR + "WallFace.png");
    private final Image wallTop = ImgLoader.sheet(DIR + "WallTop.png");
    private final Image torchImg = ImgLoader.sheet(DIR + "Torch.png");
    private final Image decorImg = ImgLoader.sheet(DIR + "Decor.png");
    private final Image bannerImg = ImgLoader.sheet(DIR + "Banner.png");
    private final Image stairs = ImgLoader.sheet(DIR + "Stairs.png");

    /**
     * A light source in world coordinates.
     * @param x world x
     * @param y world y
     * @param radius light radius in px
     * @param color light colour (alpha = intensity)
     * @param flicker how strongly it flickers (0 = steady)
     * @param seed phase offset of the flicker
     */
    public record Light(double x, double y, double radius, Color color, double flicker, double seed) {
    }

    /**
     * Creates a new DungeonPainter and lays out all decorations of the level.
     * @param level the level to paint
     */
    public DungeonPainter(Level level) {
        this.level = level;
        this.width = level.getWidth();
        this.height = level.getHeight();
        floorVariant = new byte[width][height];
        faceVariant = new byte[width][height];
        decor = new byte[width][height];
        torch = new boolean[width][height];
        banner = new boolean[width][height];
        columnTheme = new DungeonTheme[width];
        assignThemes();
        layOut();
    }

    private void assignThemes() {
        for (int x = 0; x < width; x++) {
            columnTheme[x] = DungeonTheme.CORRIDOR;
        }
        for (Arena arena : level.getArenas()) {
            DungeonTheme theme = DungeonTheme.of(arena.getType());
            for (int x = arena.getStartX(); x < Math.min(width, arena.getStartX() + arena.getWidth()); x++) {
                columnTheme[x] = theme;
            }
        }
    }

    private static int hash(int x, int y, int salt) {
        int h = x * 374761393 + y * 668265263 + salt * 1442695041;
        h = (h ^ (h >>> 13)) * 1274126177;
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }

    private boolean isWall(int x, int y) {
        return level.getTile(x, y) == TileType.WALL;
    }

    private boolean isFace(int x, int y) {
        return isWall(x, y) && !isWall(x, y + 1) && y + 1 < height;
    }

    private void layOut() {
        for (int y = 0; y < height; y++) {
            int lastTorch = -100;
            for (int x = 0; x < width; x++) {
                decor[x][y] = -1;
                if (isWall(x, y)) {
                    layOutWall(x, y, lastTorch);
                    if (torch[x][y]) {
                        lastTorch = x;
                    }
                } else {
                    layOutFloor(x, y);
                }
            }
        }
    }

    private void layOutWall(int x, int y, int lastTorch) {
        int h = hash(x, y, 1);
        int roll = h % 100;
        if (roll < 8) {
            faceVariant[x][y] = FACE_CRACKED;
        } else if (roll < 15) {
            faceVariant[x][y] = FACE_MOSSY;
        } else if (roll < 20) {
            faceVariant[x][y] = FACE_CHAINS;
        } else {
            faceVariant[x][y] = (byte) ((h >>> 8) % 3);
        }
        if (!isFace(x, y)) {
            return;
        }
        if (x - lastTorch > 4 && hash(x, y, 2) % 5 == 0) {
            torch[x][y] = true;
            lights.add(new Light((x + 0.5) * TILE_SIZE, (y + 0.9) * TILE_SIZE, 120, TORCH_LIGHT, 1, h % 97));
        } else if (columnTheme[x].getBanner() >= 0 && hash(x, y, 3) % 9 == 0) {
            banner[x][y] = true;
        }
    }

    private void layOutFloor(int x, int y) {
        int h = hash(x, y, 4);
        int roll = h % 100;
        if (roll < 70) {
            floorVariant[x][y] = (byte) ((h >>> 8) % 4);
        } else if (roll < 78) {
            floorVariant[x][y] = 4;
        } else if (roll < 84) {
            floorVariant[x][y] = 5;
        } else if (roll < 88) {
            floorVariant[x][y] = 6;
        } else if (roll < 90) {
            floorVariant[x][y] = 7;
        } else {
            floorVariant[x][y] = (byte) ((h >>> 8) % 4);
        }

        TileType type = level.getTile(x, y);
        if (type != TileType.FLOOR && type != TileType.SPAWN_FLOOR) {
            return;
        }
        int d = hash(x, y, 5);
        if (isWall(x, y - 1) && isWall(x - 1, y) && d % 10 < 7) {
            decor[x][y] = (byte) Decor.COBWEB.ordinal();
            return;
        }
        if (d % 1000 < 28) {
            List<Decor> options = columnTheme[x].getDecorations();
            Decor chosen = options.get((d >>> 8) % options.size());
            decor[x][y] = (byte) chosen.ordinal();
            if (chosen.glows() && (d >>> 4) % 2 == 0) {
                lights.add(new Light((x + 0.5) * TILE_SIZE, (y + 0.5) * TILE_SIZE, chosen.getLightRadius(),
                        chosen.getLight(), chosen == Decor.CANDLES ? 0.6 : 0.25, d % 61));
            }
        }
    }

    /**
     * Theme of the dungeon at the given world x coordinate.
     */
    public DungeonTheme themeAt(double worldX) {
        int x = (int) (worldX / TILE_SIZE);
        if (x < 0 || x >= width) {
            return DungeonTheme.CORRIDOR;
        }
        return columnTheme[x];
    }

    /**
     * Draws all visible tiles with decorations.
     * @param gc graphics context
     * @param camX camera x
     * @param camY camera y
     * @param viewW viewport width
     * @param viewH viewport height
     * @param time seconds since the renderer started (for animated tiles)
     */
    public void drawTiles(GraphicsContext gc, double camX, double camY, double viewW, double viewH, double time) {
        int minX = Math.max(0, (int) Math.floor(camX / TILE_SIZE));
        int minY = Math.max(0, (int) Math.floor(camY / TILE_SIZE));
        int maxX = Math.min(width - 1, (int) ((camX + viewW) / TILE_SIZE) + 1);
        int maxY = Math.min(height - 1, (int) ((camY + viewH) / TILE_SIZE) + 1);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                double sx = Math.round(x * TILE_SIZE - camX);
                double sy = Math.round(y * TILE_SIZE - camY);
                if (isWall(x, y)) {
                    drawWall(gc, x, y, sx, sy, time);
                } else {
                    drawFloor(gc, x, y, sx, sy, time);
                }
            }
        }
    }

    private void drawWall(GraphicsContext gc, int x, int y, double sx, double sy, double time) {
        if (isFace(x, y)) {
            gc.drawImage(wallFace, faceVariant[x][y] * SRC, 0, SRC, SRC, sx, sy, TILE_SIZE, TILE_SIZE);
            tint(gc, x, sx, sy, 0.6);
            if (torch[x][y]) {
                int frame = (int) (time * 9 + x * 3) % 4;
                gc.drawImage(torchImg, frame * SRC, 0, SRC, SRC, sx, sy, TILE_SIZE, TILE_SIZE);
            } else if (banner[x][y]) {
                gc.drawImage(bannerImg, columnTheme[x].getBanner() * SRC, 0, SRC, SRC, sx, sy, TILE_SIZE, TILE_SIZE);
            }
            return;
        }
        gc.drawImage(wallTop, (faceVariant[x][y] % 2) * SRC, 0, SRC, SRC, sx, sy, TILE_SIZE, TILE_SIZE);
        gc.setFill(RIM);
        if (!isWall(x - 1, y)) {
            gc.fillRect(sx, sy, 2, TILE_SIZE);
        }
        if (!isWall(x + 1, y)) {
            gc.fillRect(sx + TILE_SIZE - 2, sy, 2, TILE_SIZE);
        }
        if (!isWall(x, y - 1)) {
            gc.fillRect(sx, sy, TILE_SIZE, 2);
        }
    }

    private void drawFloor(GraphicsContext gc, int x, int y, double sx, double sy, double time) {
        TileType type = level.getTile(x, y);
        if (type == TileType.EXIT) {
            gc.drawImage(stairs, 0, 0, SRC, SRC, sx, sy, TILE_SIZE, TILE_SIZE);
            double pulse = 0.5 + 0.5 * Math.sin(time * 3 + y);
            gc.setFill(Color.rgb(170, 120, 255, 0.10 + 0.12 * pulse));
            gc.fillRect(sx, sy, TILE_SIZE, TILE_SIZE);
            return;
        }
        gc.drawImage(floor, floorVariant[x][y] * SRC, 0, SRC, SRC, sx, sy, TILE_SIZE, TILE_SIZE);
        tint(gc, x, sx, sy, 1);

        if (decor[x][y] >= 0) {
            gc.drawImage(decorImg, decor[x][y] * SRC, 0, SRC, SRC, sx, sy, TILE_SIZE, TILE_SIZE);
        }
        drawWallShadows(gc, x, y, sx, sy);
    }

    private void tint(GraphicsContext gc, int x, double sx, double sy, double strength) {
        Color tint = columnTheme[x].getTint();
        if (tint == null) {
            return;
        }
        gc.setFill(Color.color(tint.getRed(), tint.getGreen(), tint.getBlue(), tint.getOpacity() * strength));
        gc.fillRect(sx, sy, TILE_SIZE, TILE_SIZE);
    }

    private void drawWallShadows(GraphicsContext gc, int x, int y, double sx, double sy) {
        if (isWall(x, y - 1)) {
            gc.setFill(Color.rgb(4, 2, 10, 0.45));
            gc.fillRect(sx, sy, TILE_SIZE, 4);
            gc.setFill(Color.rgb(4, 2, 10, 0.28));
            gc.fillRect(sx, sy + 4, TILE_SIZE, 4);
            gc.setFill(Color.rgb(4, 2, 10, 0.14));
            gc.fillRect(sx, sy + 8, TILE_SIZE, 6);
        }
        if (isWall(x - 1, y)) {
            gc.setFill(Color.rgb(4, 2, 10, 0.30));
            gc.fillRect(sx, sy, 4, TILE_SIZE);
            gc.setFill(Color.rgb(4, 2, 10, 0.14));
            gc.fillRect(sx + 4, sy, 4, TILE_SIZE);
        }
        if (isWall(x + 1, y)) {
            gc.setFill(Color.rgb(4, 2, 10, 0.18));
            gc.fillRect(sx + TILE_SIZE - 4, sy, 4, TILE_SIZE);
        }
    }

    /**
     * Darkens everything outside the hero's light and adds glows of torches,
     * glowing decorations and the given dynamic lights.
     * @param gc graphics context
     * @param camX camera x
     * @param camY camera y
     * @param viewW viewport width
     * @param viewH viewport height
     * @param heroX hero x on screen
     * @param heroY hero y on screen
     * @param time seconds since start
     * @param dynamic extra lights (projectiles, effects) in world coordinates
     */
    public void drawLighting(GraphicsContext gc, double camX, double camY, double viewW, double viewH,
                             double heroX, double heroY, double time, List<Light> dynamic) {
        double radius = Math.max(viewW, viewH) * 0.55;
        RadialGradient darkness = new RadialGradient(0, 0, heroX, heroY, radius, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(6, 3, 14, 0.05)),
                new Stop(0.22, Color.rgb(6, 3, 14, 0.18)),
                new Stop(0.55, Color.rgb(6, 3, 14, 0.66)),
                new Stop(1, Color.rgb(6, 3, 14, 0.92)));
        gc.setFill(darkness);
        gc.fillRect(0, 0, viewW, viewH);

        gc.setGlobalBlendMode(BlendMode.ADD);
        for (Light light : lights) {
            drawLight(gc, light, camX, camY, viewW, viewH, time);
        }
        for (Light light : dynamic) {
            drawLight(gc, light, camX, camY, viewW, viewH, time);
        }
        drawExitGlow(gc, camX, camY, viewW, viewH, time);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
    }

    private void drawExitGlow(GraphicsContext gc, double camX, double camY, double viewW, double viewH, double time) {
        int x = width - 1;
        for (int y = 0; y < height; y++) {
            if (level.getTile(x, y) == TileType.EXIT) {
                drawLight(gc, new Light((x + 0.5) * TILE_SIZE, (y + 0.5) * TILE_SIZE, 70, EXIT_LIGHT, 0.4, y),
                        camX, camY, viewW, viewH, time);
            }
        }
    }

    private void drawLight(GraphicsContext gc, Light light, double camX, double camY, double viewW, double viewH, double time) {
        double sx = light.x() - camX;
        double sy = light.y() - camY;
        double r = light.radius();
        if (sx + r < 0 || sy + r < 0 || sx - r > viewW || sy - r > viewH) {
            return;
        }
        double wobble = 1 + light.flicker() * (0.07 * Math.sin(time * 11 + light.seed()) + 0.05 * Math.sin(time * 23 + light.seed() * 2));
        r *= wobble;
        Color c = light.color();
        RadialGradient glow = new RadialGradient(0, 0, sx, sy, r, false, CycleMethod.NO_CYCLE,
                new Stop(0, c),
                new Stop(0.4, Color.color(c.getRed(), c.getGreen(), c.getBlue(), c.getOpacity() * 0.45)),
                new Stop(1, Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0)));
        gc.setFill(glow);
        gc.fillOval(sx - r, sy - r, r * 2, r * 2);
    }
}
