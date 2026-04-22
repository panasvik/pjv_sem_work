package cz.fel.cvut.pjv.dgnmstr.view;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import cz.fel.cvut.pjv.dgnmstr.view.GameRenderer;
import cz.fel.cvut.pjv.dgnmstr.global.GameEngine;
import javafx.scene.control.Label;

public class GameController {


    @FXML private Pane canvasWrapper;
    @FXML private Canvas gameCanvas;

    @FXML
    public void initialize() {
        gameCanvas.widthProperty().bind(canvasWrapper.widthProperty());
        gameCanvas.heightProperty().bind(canvasWrapper.heightProperty());

//        GameRenderer renderer = new GameRenderer(gameCanvas);
//
//        GameEngine.getInstance().setRenderer(renderer);
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
