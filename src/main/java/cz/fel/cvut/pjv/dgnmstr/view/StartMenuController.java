package cz.fel.cvut.pjv.dgnmstr.view;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class StartMenuController {

    @FXML
    public void SinglePLayer() {
        SceneManager mngr = SceneManager.getInstance();
        mngr.activate(View.SINGLEPLAYER);
    }

    @FXML
    public void MultiPlayer() {

    }

    @FXML
    public void exitGame() {
        Platform.exit();
    }
 }
