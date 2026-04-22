package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.GameApp;
import cz.fel.cvut.pjv.dgnmstr.entity.WarriorController;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Warrior;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;

public class MenuController {

    private View prevView;

    public void setPrevView(View prevView) {
        this.prevView = prevView;
    }

    @FXML
    public void newLevel() {
        GameManager gameManager = GameManager.getInstance();
        gameManager.newLevel();
        gameManager.loadPlayersToLevel();
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.activate(View.LEVEL);
        Scene scene = sceneManager.getMainScene();
        gameManager.startLevel();
    }

    /**
     * transports player to see his stats, inventory, equipped items
     */
    @FXML
    public void HeroView() {

    }

    @FXML
    public void back() {
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.activate(View.SINGLEPLAYER);
    }
}
