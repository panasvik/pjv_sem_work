package cz.fel.cvut.pjv.dgnmstr.view;


import cz.fel.cvut.pjv.dgnmstr.global.ImgLoader;
import cz.fel.cvut.pjv.dgnmstr.global.InputHandler;
import cz.fel.cvut.pjv.dgnmstr.global.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.item.Item;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class GameController {
    private static final Logger log = Logger.getLogger(GameController.class.getName());
    private final GameManager gameManager;

    @FXML
    private Pane canvasWrapper;

    @FXML
    private Canvas gameCanvas;

    @FXML
    private VBox inventoryView;

    @FXML
    private ListView<Item> inventoryList;

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
        ImgLoader.loadAll();

        // Setup custom cell factory for inventory list to show item names
        inventoryList.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getPrice() + " gold");
                }
            }
        });
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
            return false;
        }

        if (renderer != null) {
            log.info("starting");
            return true;
        }
        InputHandler inputHandler = new InputHandler(SceneManager.getInstance().getMainScene());
        clientManager.setInputHandler(inputHandler);
        gameManager.setIh(inputHandler);
        renderer = new GameRenderer(gameCanvas, clientManager.getLc().getLevel(), clientManager.getLocalHero());
        gameManager.setGm(renderer);
        renderer.start();
        gameManager.setGameController(this);

        if (SceneManager.getInstance().getMainScene() != null) {
            SceneManager.getInstance().getMainScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.I) {
                    toggleInventory();
                }
            });
        }

        clientManager.startManagingInputs();
        log.info("GameController started level systems");
        return true;
    }

    public void toggleInventory() {
        if (SceneManager.getInstance().getMainScene() == null) return;
        SceneManager.getInstance().getMainScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.I) {
                if (inventoryView.isVisible()) {
                    hideInventory();
                } else {
                    showInventory();
                }
            }
        });
    }

    public void showInventory() {
        Inventory inventory = gameManager.getCm().getMyProfile().getInventory();
        ObservableList<Item> items = FXCollections.observableArrayList(inventory.getInvent());
        inventoryList.setItems(items);
        inventoryView.setVisible(true);
        log.info("Showing inventory");
    }

    @FXML
    public void hideInventory() {
        inventoryView.setVisible(false);
        log.info("Hiding inventory");
    }

}
