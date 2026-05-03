package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TileType;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import cz.fel.cvut.pjv.dgnmstr.global.ImgLoader;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GameRenderer extends AnimationTimer {
    // this class should render level view
    private final Level level;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private static final Logger log = Logger.getLogger(GameRenderer.class.getName());
    private final Hero localPlayer;

    public GameRenderer(Canvas canvas, Level level, Hero localPlayer) {
        this.canvas = canvas;
        this.level = level;
        this.gc = canvas.getGraphicsContext2D();
        this.localPlayer = localPlayer;
    }

    @Override
    public void handle(long now) {
        if (level == null || localPlayer == null) {
            return;
        }

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0) {
            return;
        }
        double worldWidth = level.getWidth() * Level.TILE_SIZE;
        double worldHeight = level.getHeight() * Level.TILE_SIZE;
        double camX = Math.max(0, Math.min(localPlayer.getX() - canvas.getWidth() / 2, Math.max(0, worldWidth - canvas.getWidth())));
        double camY = Math.max(0, Math.min(localPlayer.getY() - canvas.getHeight() / 2, Math.max(0, worldHeight - canvas.getHeight())));

        int minTileX = Math.max(0, (int) (camX / Level.TILE_SIZE));
        int minTileY = Math.max(0, (int) (camY / Level.TILE_SIZE));
        int maxTileX = Math.min(level.getWidth() - 1, (int) ((camX + canvas.getWidth()) / Level.TILE_SIZE) + 1);
        int maxTileY = Math.min(level.getHeight() - 1, (int) ((camY + canvas.getHeight()) / Level.TILE_SIZE) + 1);

        for (int x = minTileX; x <= maxTileX; x++) {
            for (int y = minTileY; y <= maxTileY; y++ ) {
                TileType tile = level.getTile(x,y);
                switch (tile) {
                    case WALL:
                        gc.setFill(Color.BLACK);
                        break;
                    case FLOOR:
                        gc.setFill(Color.DARKGRAY);
                        break;
                    case SPAWN_FLOOR:
                        gc.setFill(Color.GRAY);
                        break;
                    case CHEST_FLOOR:
                        gc.setFill(Color.GRAY);
                        break;
                    case EXIT:
                        gc.setFill(Color.GRAY);
                        break;
                    default:
                        gc.setFill(Color.WHITE);
                        break;
                }
                gc.fillRect(x * Level.TILE_SIZE - camX, y * Level.TILE_SIZE - camY, Level.TILE_SIZE, Level.TILE_SIZE);

            }
        }
        List<Entity> entitySnapshot = new ArrayList<>(level.getAllEntities());
        for (Entity e : entitySnapshot){
            double drawX = e.getX() - camX;
            double drawY = e.getY() - camY;
            if (drawX + Level.TILE_SIZE < 0 || drawY + Level.TILE_SIZE < 0 ||
                    drawX > canvas.getWidth() || drawY > canvas.getHeight()) {
                continue;
            }
            if (e.isAlive()) {
                if (e.getImgId() != null) {
                    gc.drawImage(ImgLoader.get(e.getImgId()), 0, 0,e.getImgId().getFrameWidth(), e.getImgId().getFrameHeight(), drawX, drawY, Level.TILE_SIZE, Level.TILE_SIZE);
                } else {
                    gc.setFill(Color.RED);
                    gc.fillRect(drawX, drawY, Level.TILE_SIZE, Level.TILE_SIZE);
                }
            }
        }

    }
}