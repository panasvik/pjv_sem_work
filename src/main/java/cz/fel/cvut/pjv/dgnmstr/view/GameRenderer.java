package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TileType;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.logging.Logger;

public class GameRenderer extends AnimationTimer {
    // this class should render level view
    private final Level level;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private static final Logger log = Logger.getLogger(GameRenderer.class.getName());

    public GameRenderer(Canvas canvas, Level level) {
        this.canvas = canvas;
        this.level = level;
        this.gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void handle(long now) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Hero player = level.getPlayers().get(0);
        double camX = Math.max(0, player.getX() - canvas.getWidth() / 2);
        double camY = Math.max(0, player.getY() - canvas.getHeight() / 2);
        log.info("Player pos: " + player.getX() + ", " + player.getY());

        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++ ) {
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
        for (Entity e : level.getAllEntities()){
            gc.setFill(Color.RED);
            if (e.isAlive()) {
                gc.fillRect(e.getX() - camX, e.getY() - camY, Level.TILE_SIZE, Level.TILE_SIZE);//for now...
            }
        }

    }
}