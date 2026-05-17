package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TileType;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.Projectile;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.global.ImgLoader;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class GameRenderer extends AnimationTimer {
    // this class should render level view
    private final Level level;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Hero localHero;
    private final GameManager gameManager;

    public GameRenderer(Canvas canvas, Level level, Hero localHero) {
        this.canvas = canvas;
        this.level = level;
        this.gc = canvas.getGraphicsContext2D();
        this.localHero = localHero;
        gameManager = GameManager.getInstance();
    }

    @Override
    public void handle(long now) {
        if (level == null || localHero == null) {
            return;
        }
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0) {
            return;
        }
        double worldWidth = level.getWidth() * Level.TILE_SIZE;
        double worldHeight = level.getHeight() * Level.TILE_SIZE;
        double camX = Math.max(0, Math.min(localHero.getX() - canvas.getWidth() / 2, Math.max(0, worldWidth - canvas.getWidth())));
        double camY = Math.max(0, Math.min(localHero.getY() - canvas.getHeight() / 2, Math.max(0, worldHeight - canvas.getHeight())));

        drawLevel(camX, camY);
        drawEntities(camX, camY);
        drawProjectiles(camX, camY);
        gameManager.getGameController().updateHealthBar(localHero.getHp(), localHero.getMaxHP());
        gameManager.getGameController().setGold(localHero.getGold());

    }

    private void drawLevel(double camX, double camY) {
        int minTileX = Math.max(0, (int) (camX / Level.TILE_SIZE));
        int minTileY = Math.max(0, (int) (camY / Level.TILE_SIZE));
        int maxTileX = Math.min(level.getWidth() - 1, (int) ((camX + canvas.getWidth()) / Level.TILE_SIZE) + 1);
        int maxTileY = Math.min(level.getHeight() - 1, (int) ((camY + canvas.getHeight()) / Level.TILE_SIZE) + 1);

        for (int x = minTileX; x <= maxTileX; x++) {
            for (int y = minTileY; y <= maxTileY; y++ ) {
                TileType tile = level.getTile(x,y);
                switch (tile) {
                    case WALL:
                        gc.drawImage(ImgLoader.get(ImgId.WALL),x * Level.TILE_SIZE - camX, y * Level.TILE_SIZE - camY, Level.TILE_SIZE, Level.TILE_SIZE);
                        break;
                    case FLOOR, SPAWN_FLOOR, CHEST_FLOOR, EXIT, FENCE_FLOOR:
                        gc.drawImage(ImgLoader.get(ImgId.TILE),x * Level.TILE_SIZE - camX, y * Level.TILE_SIZE - camY, Level.TILE_SIZE, Level.TILE_SIZE);
                        break;
                    default:
                        gc.setFill(Color.WHITE);
                        break;
                }

            }
        }
    }

    private void drawEntities(double camX, double camY) {
        List<Entity> entitySnapshot = new ArrayList<>(level.getAllEntities());
        for (Entity e : entitySnapshot){
            if (e.isAlive()) {
                if (e.getImgId() != null) {
                    ImgId imgId = e.getImgId();
                    double drawWidth = Level.TILE_SIZE * imgId.getScale();
                    double drawHeight = Level.TILE_SIZE * imgId.getScale();
                    double drawX = e.getX() - camX - drawWidth / 2;
                    double drawY = e.getY() - camY - drawHeight / 2;
                    if (drawX + drawWidth < 0 || drawY + drawHeight < 0 ||
                            drawX > canvas.getWidth() || drawY > canvas.getHeight()) {
                        continue;
                    }
                    gc.drawImage(ImgLoader.get(imgId), 0, 0, imgId.getFrameWidth(), imgId.getFrameHeight(), drawX, drawY, drawWidth, drawHeight);
                } else {
                    double drawX = e.getX() - camX - Level.TILE_SIZE / 2.0;
                    double drawY = e.getY() - camY - Level.TILE_SIZE / 2.0;
                    if (drawX + Level.TILE_SIZE < 0 || drawY + Level.TILE_SIZE < 0 ||
                            drawX > canvas.getWidth() || drawY > canvas.getHeight()) {
                        continue;
                    }
                    gc.setFill(Color.RED);
                    gc.fillRect(drawX, drawY, Level.TILE_SIZE, Level.TILE_SIZE);
                }
            }
        }
    }

    private void drawProjectiles(double camX, double camY){
        List<Projectile> projectiles = new ArrayList<>(level.getProjectiles());
        for (Projectile p : projectiles){
            double drawX = p.getX() - camX;
            double drawY = p.getY() - camY;

            ImgId imgId = p.getImgId();
            double drawWidth = imgId.getWidth() * imgId.getScale();
            double drawHeight = imgId.getHeight() * imgId.getScale();
            if (drawWidth + drawX < 0 || drawY + drawHeight < 0 || drawX - drawWidth > canvas.getWidth() || drawY -drawHeight > canvas.getHeight()){
                continue;
            }
            double angle = Math.toDegrees(Math.atan2(p.getDeltaY(), p.getDeltaX()));
            gc.save();
            gc.translate(drawX, drawY);
            gc.rotate(angle);
            gc.drawImage(ImgLoader.get(imgId), 0, 0, imgId.getFrameWidth(), imgId.getFrameHeight(), -drawWidth / 2, -drawHeight / 2, drawWidth, drawHeight);
            gc.restore();

        }
    }

}
