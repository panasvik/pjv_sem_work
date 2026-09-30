package cz.fel.cvut.pjv.dgnmstr.view;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SceneManager {
    private static SceneManager instance;

    private Stage mainStage;
    private Scene mainScene;

    private final Map<View, Parent> screens = new HashMap<>();

    private final Map<View, Object> controllers = new HashMap<>();


    private static final Logger log =  Logger.getLogger(SceneManager.class.getName());
    private static final Duration FADE_OUT = Duration.millis(250);
    private static final Duration FADE_IN = Duration.millis(450);

    private FadeTransition currentFade;


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
        this.mainScene.setFill(Color.BLACK);
        this.mainStage.setScene(mainScene);
    }

    public void loadScreen(View view) {
        try {
            URL resource = SceneManager.class.getResource(view.getRelativePath());
            if (resource == null) {
                log.severe("View path '" + view.getRelativePath() + "' not found");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            screens.put(view, root);

            Object controller = loader.getController();
            if (controller != null) {
                controllers.put(view, controller);
            }

        } catch (IOException e) {
            log.severe("Unable to load view: " + e.getMessage());
        }
    }

    /**
     * Switches to the given screen, fading the current one out and the new one in.
     * @param view the screen to show
     */
    public void activate(View view) {
        Parent root = screens.get(view);
        if (root == null) {
            log.severe("View not found. Load it first.");
            return;
        }
        if (currentFade != null) {
            currentFade.stop();
        }
        Parent old = mainScene.getRoot();
        boolean showing = mainStage != null && mainStage.isShowing();
        if (old == null || old == root || !showing) {
            show(view, root);
            return;
        }
        FadeTransition fadeOut = new FadeTransition(FADE_OUT, old);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            show(view, root);
            old.setOpacity(1);
        });
        currentFade = fadeOut;
        fadeOut.play();
    }

    private void show(View view, Parent root) {
        root.setOpacity(0);
        mainScene.setRoot(root);

        Object controller = controllers.get(view);
        if (controller instanceof Reappearance) {
            ((Reappearance) controller).reappear();
        }

        FadeTransition fadeIn = new FadeTransition(FADE_IN, root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        currentFade = fadeIn;
        fadeIn.play();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Scene getMainScene() {
        return mainScene;
    }
    public void registerScreen(View view, Parent root){
        screens.put(view, root);
    }
}