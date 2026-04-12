package cz.fel.cvut.pjv.dgnmstr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameApp extends Application {
    private static final Logger log =  Logger.getLogger(GameApp.class.getName());
    @Override
    public void start(Stage stage) throws IOException {
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
        FXMLLoader fxmlLoader = new FXMLLoader(GameApp.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Dungeon Mas");
        stage.setScene(scene);
        stage.show();
    }
}
