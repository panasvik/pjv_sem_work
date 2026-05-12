package cz.fel.cvut.pjv.dgnmstr;

import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.view.SceneManager;
import cz.fel.cvut.pjv.dgnmstr.view.View;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.*;

import static javafx.scene.text.Font.loadFont;

public class GameApp extends Application {
    private static final Logger log = Logger.getLogger(GameApp.class.getName());


    @Override
    public void start(Stage stage) {
        loadFonts();
        boolean isLog = getParameters().getRaw().contains("--log=true");
        setLog(isLog);

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
        if (gm.getServerManager() != null) {
            gm.getServerManager().stop();
        }

        // Clean up client threads
        if (gm.getClientNetworkManager() != null) {
            gm.getClientNetworkManager().disconnect();
        }
        if (gm.getClientManager() != null) {
            gm.getClientManager().stopManagingInputs();
        }
    }

    private void setLog(boolean isLog) {
        Logger mainLogger = Logger.getLogger("");

        for(Handler handler : mainLogger.getHandlers()){
            handler.setLevel(Level.ALL);
        }

        Logger gameLogger = Logger.getLogger("cz.fel.cvut.pjv.dgnmstr");
        if (isLog) {
            mainLogger.setLevel(Level.INFO);
            gameLogger.setLevel(Level.ALL);

            try {
                FileHandler fileHandler = new FileHandler("logs/game.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                mainLogger.addHandler(fileHandler);

            } catch (IOException | SecurityException e) {
                log.severe("unable to save logs to file!");
            }

            log.info("Logging is on");
        } else {
            mainLogger.setLevel(Level.SEVERE);
        }
    }

    public void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/cz/fel/cvut/pjv/dgnmstr/view/PirataOne-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/cz/fel/cvut/pjv/dgnmstr/view/Cinzel-Bold.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/cz/fel/cvut/pjv/dgnmstr/view/EBGaramond-Bold.ttf"), 12);

    }
}
