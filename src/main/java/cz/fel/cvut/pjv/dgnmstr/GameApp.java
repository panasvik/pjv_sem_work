package cz.fel.cvut.pjv.dgnmstr;

import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.view.SceneManager;
import cz.fel.cvut.pjv.dgnmstr.view.View;
import javafx.application.Application;
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


            log.info("Logging is on");
        } else {
            mainLogger.setLevel(Level.SEVERE);
        }

        GameManager gameManager = GameManager.getInstance();
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.init(stage, 1280, 720);
        sceneManager.loadScreen(View.START_MENU);
        sceneManager.loadScreen(View.LEVEL);

        sceneManager.activate(View.START_MENU);
        stage.setTitle("Dungeon Master");
        stage.show();

    }

    @Override
    public void stop() {
        log.info("Shutting down game and cleaning up threads...");
        GameManager gm = GameManager.getInstance();

        // Clean up server threads
        if (gm.getSm() != null) {
            gm.getSm().stop();
        }

        // Clean up client threads
        if (gm.getCnm() != null) {
            gm.getCnm().disconnect();
        }
        if (gm.getCm() != null) {
            gm.getCm().stopManagingInputs();
        }

        // Force the JVM to terminate and kill any lingering threads
    }

    public Stage getStage() {
        return stage;
    }

    public static GameApp getInstance() {
        return instance;
    }

   }
