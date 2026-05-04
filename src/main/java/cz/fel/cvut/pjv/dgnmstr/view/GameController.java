package cz.fel.cvut.pjv.dgnmstr.view;


import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
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

import static cz.fel.cvut.pjv.dgnmstr.entity.map.Chest.PRICE_TO_OPEN_CHEST;

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
    private VBox chestView;

    @FXML
    private ListView<Item> inventoryList;

    @FXML
    private ListView<Item> chestList;


    private Chest currentChest;
    private GameRenderer renderer;

    public GameController() {
        this.gameManager = GameManager.getInstance();
    }

    @FXML
    public void initialize() {
        gameCanvas.widthProperty().bind(canvasWrapper.widthProperty());
        gameCanvas.heightProperty().bind(canvasWrapper.heightProperty());
        startWhenClientStateReady();
        ImgLoader.loadAll();

        fillList(inventoryList);
        fillList(chestList);
        setupListInteractions();
    }

    private void fillList(ListView<Item> itemList) {
        itemList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    private void startWhenClientStateReady() {
        AnimationTimer startupWaiter = new AnimationTimer() {
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
        ClientNetworkManager cnm = gameManager.getCnm();
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
        
        SceneManager.getInstance().getMainScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.I) {
                if (inventoryView.isVisible()) {
                    hideInventory();
                } else {
                    showInventory();
                }
            } else if (event.getCode() == KeyCode.F) {
                if (chestView.isVisible()) {
                    hideChest();
                } else {
                    showChest();
                }
            }
        });

        clientManager.startManagingInputs();
        log.info("GameController started level systems");
        return true;
    }

    public void showChest() {
        Hero hero = gameManager.getCm().getLocalHero();
        currentChest = gameManager.getCm().getnearestChest();
        if (currentChest == null) {
            log.info("No chest in range");
            return;
        }
        if (!currentChest.interact(hero)) return;
        log.info("player " + hero + " opened " + currentChest + " and got " + hero.getGold() + " gold left");
        Inventory inventory = currentChest.getInventory();
        ObservableList<Item> items = FXCollections.observableArrayList(inventory.getInvent());
        chestList.setItems(items);
        chestView.setVisible(true);
    }

    public void hideChest() {
        chestView.setVisible(false);
    }

    public void showInventory() {
        Inventory inventory = gameManager.getCm().getMyProfile().getInventory();
        ObservableList<Item> items = FXCollections.observableArrayList(inventory.getInvent());
        inventoryList.setItems(items);
        inventoryView.setVisible(true);
    }

    @FXML
    public void hideInventory() {
        inventoryView.setVisible(false);
    }

    private void setupListInteractions() {
        chestList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Trigger on double-click
                Item selectedItem = chestList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    takeItemFromChest(selectedItem);
                }
            }
        });

        inventoryList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Item selectedItem = inventoryList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    if (chestView.isVisible()) {
                        storeItemInChest(selectedItem);
                    } /*else {
                        useItem(selectedItem);
                    }*/
                }
            }
        });
    }

    public void takeItemFromChest(Item selectedItem) {
        if (gameManager.getCm().getMyProfile().getInventory().addItem(selectedItem)) {
            inventoryList.getItems().add(selectedItem);

        }
        if (currentChest.getInventory().remove(selectedItem)) {
            chestList.getItems().remove(selectedItem);
        }
    }

    public void storeItemInChest(Item selectedItem) {
        if (gameManager.getCm().getMyProfile().getInventory().remove(selectedItem)) {
            inventoryList.getItems().remove(selectedItem);

        }
        if (currentChest.getInventory().addItem(selectedItem)) {
            chestList.getItems().add(selectedItem);
        }
    }
}
