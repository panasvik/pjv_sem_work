package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;

    // renderer needs only Canvas
    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void render(Level level) {
        if (level == null) return;

        // reset screen
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // write new coords of the camera based on players warrior position
        double cameraX = 0; // count here
        double cameraY = 0;

        // rendering map
        // ...

        // render entities
        for (Entity e : level.getAllEntities()) {
            // red squares here for example
            double screenX = e.getX() - cameraX;
            double screenY = e.getY() - cameraY;

            gc.setFill(Color.RED);
            gc.fillRect(screenX, screenY, 32, 32);
        }
    }
}