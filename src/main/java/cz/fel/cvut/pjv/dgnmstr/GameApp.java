package cz.fel.cvut.pjv.dgnmstr;

import cz.fel.cvut.pjv.dgnmstr.global.GameEngine;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameApp extends Application {
    private static GameApp instance;
    private static final Logger log =  Logger.getLogger(GameApp.class.getName());
    private Stage stage;



    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        this.stage = stage;
        boolean isLog = getParameters().getRaw().contains("--log=true");
        Logger mainLogger = Logger.getLogger("");

        for(Handler handler : mainLogger.getHandlers()){
            handler.setLevel(Level.ALL);
        }

        Logger gameLogger = Logger.getLogger("cz.fel.cvut.pjv.dgnmstr");
        if (isLog) {
            mainLogger.setLevel(Level.INFO);
            gameLogger.setLevel(Level.ALL);


            log.info("Logování zapnuto.");
        } else {
            mainLogger.setLevel(Level.SEVERE);
        }

        GameEngine engine = GameEngine.getInstance();
        GameManager manager = GameManager.getInstance();

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.init(stage, 1280, 720);
        sceneManager.loadScreen(View.MAIN_MENU);
        sceneManager.loadScreen(View.SINGLEPLAYER);
        sceneManager.loadScreen(View.START_MENU);

        sceneManager.activate(View.START_MENU);
        stage.setTitle("Dungeon Master");
        stage.show();

    }

    public Stage getStage() {
        return stage;
    }

    public static GameApp getInstance() {
        return instance;
    }
}
