package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.global.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.global.InputHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;

public class MenuController {

    private View prevView;

    public void setPrevView(View prevView) {
        this.prevView = prevView;
    }

    @FXML
    public void newLevel() {
        ClientManager gm = ClientManager.getInstance();
        SceneManager sm = SceneManager.getInstance();
        //now hardcoded, will be changed later
        gm.newLevel(23934954945L);
        gm.loadPlayersToLevel();

        sm.activate(View.LEVEL);

        Scene scene = sm.getMainScene();
        InputHandler inputHandler = new InputHandler(scene);
        gm.setInputHandler(inputHandler);

        gm.startLevel();
    }

    @FXML
    public void HeroView() {
    }

    @FXML
    public void back() {
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.activate(View.SINGLEPLAYER);
    }
}