package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.LevelController;
import cz.fel.cvut.pjv.dgnmstr.entity.WarriorController;
import cz.fel.cvut.pjv.dgnmstr.view.GameRenderer;
import javafx.animation.AnimationTimer;


public class GameEngine{
    private static GameEngine instance;
    private boolean isPaused = true;
    private boolean isStarted = false;
    private LevelController lc = null;
    private AnimationTimer gameLoop;
    private long lastUPDTime = 0;
    private GameRenderer renderer;

    private GameEngine() {
        initGameLoop();
    }

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }


    public void startLC(LevelController lc) {
        this.lc = lc;
        lc.startLevel();
        isStarted = true;
        isPaused = false;
        this.lastUPDTime = 0;
        gameLoop.start();
    }


    public void pauseEngine() {
        isPaused = true;
    }

    public void unpauseEngine() {
        isPaused = false;
        lastUPDTime = 0;
    }

    public void exitLevel() {
        isStarted = false;
        isPaused = false;
        gameLoop.stop();
        lc = null;
    }


    private void initGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUPDTime == 0) {
                    lastUPDTime = now;
                    return;
                }
                double deltaTime = (now - lastUPDTime) / 1_000_000_000.0;
                lastUPDTime = now;
                if (!isPaused && isStarted && lc != null) {

                    lc.update(deltaTime);

                    if (renderer != null) {
                        renderer.render(lc.getLevel());
                    }


                }
            }
        };
    }


    public void setRenderer(GameRenderer renderer) {
        this.renderer = renderer;
    }
}

