package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.global.InputHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import cz.fel.cvut.pjv.dgnmstr.view.GameRenderer;
import cz.fel.cvut.pjv.dgnmstr.global.GameEngine;
import javafx.scene.control.Label;

import java.util.logging.Logger;

public class GameController {
    private static final Logger log = Logger.getLogger(GameController.class.getName());

    @FXML private Pane canvasWrapper;
    @FXML private Canvas gameCanvas;

    @FXML
    public void initialize() {
        gameCanvas.widthProperty().bind(canvasWrapper.widthProperty());
        gameCanvas.heightProperty().bind(canvasWrapper.heightProperty());
        log.info("Canvas: " + gameCanvas.getWidth() + "x" + gameCanvas.getHeight());
        log.info("Wrapper: " + canvasWrapper.getWidth() + "x" + canvasWrapper.getHeight());

        Scene scene = SceneManager.getInstance().getMainScene();
        InputHandler inputHandler = new InputHandler(scene);
        GameManager.getInstance().setInputHandler(inputHandler);

        Level level = GameManager.getInstance().getLevel();

        if (level == null) {
            log.severe("Level is null!");
            return;
        }
        canvasWrapper.widthProperty().addListener((obs, oldVal, newVal) -> {
            log.info("Wrapper width changed: " + newVal);
            log.info("Canvas width: " + gameCanvas.getWidth());

            GameRenderer renderer = new GameRenderer(gameCanvas, level);
            renderer.start();
        });

        //GameEngine.getInstance().setRenderer(renderer);
    }





    /**
     * pauses a level
     */
    @FXML
    public void PauseGame() {

    }

    /**
     * resumes paused level
     */
    @FXML
    public void ResumeGame() {

    }

    /**
     * opens chest view for player
     */
    @FXML
    public void OpenChest() {

    }

    /**
     * closes chest view
     */
    @FXML
    public void CloseChest() {

    }

    /**
     * call to update Model when item taken
     */
    @FXML
    public void TakeItemFromChest() {

    }

    /**
     * shows level over screen
     */
    @FXML
    public void LevelOver() {

    }

    /**
     * shows level win screen
     */
    @FXML
    public void LevelWin() {

    }



}
