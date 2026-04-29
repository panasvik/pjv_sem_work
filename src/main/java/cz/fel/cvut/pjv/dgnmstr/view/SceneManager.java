package cz.fel.cvut.pjv.dgnmstr.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SceneManager {
    private static SceneManager instance;

    private Stage mainStage;
    private Scene mainScene;

    private final Map<View, Parent> screens = new HashMap<>();

    private static final Logger log =  Logger.getLogger(SceneManager.class.getName());


    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    /**
     *
     * @param stage
     * @param width
     * @param height
     */
    public void init(Stage stage, double width, double height) {
        this.mainStage = stage;
        this.mainScene = new Scene(new javafx.scene.layout.Pane(), width, height);
        this.mainStage.setScene(mainScene);
    }

    /**
     *
     * @param view
     */
    public void loadScreen(View view) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(view.getRelativePath()));
            screens.put(view, root);
        } catch (IOException e) {
            log.severe("View path " + view.getRelativePath() + " not found on disc");
        }
    }

    /**
     *
     * @param view
     */
    public void activate(View view) {
        Parent root = screens.get(view);
        if (root != null) {
            mainScene.setRoot(root);
        } else {
            log.severe("View path " + view.getRelativePath() + "not found in HashMap" +
                    "add it with loadScreen()");
        }
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Scene getMainScene() {
        return mainScene;
    }
}