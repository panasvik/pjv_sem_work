package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.LevelController;

public class GameEngine {
    private static GameEngine instance;
    private boolean isPaused = true;
    private LevelController lc = null;

    private GameEngine() {}

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    public void startLC(LevelController lc) {
        this.lc = lc;
        lc.startLevel();
        isPaused = false;
    }

    public void tick(double deltaTime) {
        if (!isPaused && lc != null) {
            lc.update(deltaTime);
        }
    }

    public void pauseEngine() { isPaused = true; }
    public void unpauseEngine() { isPaused = false; }
    public void exitLevel() {
        isPaused = true;
        lc = null;
    }
}