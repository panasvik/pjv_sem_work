package cz.fel.cvut.pjv.dgnmstr.view;


import cz.fel.cvut.pjv.dgnmstr.global.InputHandler;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.network.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.network.GameManager;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GameController {
    private static final Logger log = Logger.getLogger(GameController.class.getName());
    private final GameManager gameManager;
    private final ScheduledExecutorService updateExecutor = Executors.newSingleThreadScheduledExecutor();

    @FXML
    private Pane canvasWrapper;

    @FXML
    private Canvas gameCanvas;

    private InputHandler inputHandler;
    private ClientNetworkManager cnm;
    private GameRenderer renderer;
    private AnimationTimer startupWaiter;

    public GameController() {
        this.gameManager = GameManager.getInstance();
    }

    @FXML
    public void initialize() {
        gameCanvas.widthProperty().bind(canvasWrapper.widthProperty());
        gameCanvas.heightProperty().bind(canvasWrapper.heightProperty());
        startWhenClientStateReady();
    }

    private void startWhenClientStateReady() {
        startupWaiter = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (tryStartGameSystems()) {
                    stop();
                }
            }
        };
        startupWaiter.start();
    }

    private boolean tryStartGameSystems() {
        ClientManager clientManager = gameManager.getCm();
        cnm = gameManager.getCnm();
        if (cnm == null || clientManager == null || clientManager.getLc() == null || clientManager.getLocalHero() == null) {
            log.info("unable to start, no cnm or cm or lc or localHero");
            return false;
        }

        if (renderer != null) {
            log.info("starting");
            return true;
        }

        inputHandler = new InputHandler(SceneManager.getInstance().getMainScene());
        gameManager.setIh(inputHandler);
        renderer = new GameRenderer(gameCanvas, clientManager.getLc().getLevel(), clientManager.getLocalHero());
        gameManager.setGm(renderer);
        renderer.start();
        updateExecutor.scheduleAtFixedRate(this::manageInputs, 0, 16, TimeUnit.MILLISECONDS);
        log.info("GameController started level systems");
        return true;
    }

    public void manageInputs() {
        if (inputHandler == null || cnm == null) {
            return;
        }
        Inputs inputs = inputHandler.getInputs();
        cnm.sendInputs(inputs);
    }
}
