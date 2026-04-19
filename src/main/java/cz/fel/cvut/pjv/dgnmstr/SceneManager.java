package cz.fel.cvut.pjv.dgnmstr;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;

    private Stage mainStage;
    private Scene mainScene;

    private final Map<String, Parent> screens = new HashMap<>();

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
     * @param name
     * @param fxmlPath
     */
    public void loadScreen(String name, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            screens.put(name, root);
        } catch (IOException e) {
            System.err.println("Не удалось загрузить FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     *
     * @param name
     */
    public void activate(String name) {
        Parent root = screens.get(name);
        if (root != null) {
            mainScene.setRoot(root);
        } else {
            System.err.println("Экран с именем '" + name + "' не найден в кэше.");
        }
    }

    public Stage getMainStage() {
        return mainStage;
    }
}