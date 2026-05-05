package cz.fel.cvut.pjv.dgnmstr.view;


import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.global.ImgLoader;
import cz.fel.cvut.pjv.dgnmstr.global.InputHandler;
import cz.fel.cvut.pjv.dgnmstr.global.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.item.armor.ArmorPieceType;
import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.item.Item;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
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
    private ProgressBar healthBar;

    private DoubleProperty maxHealth;
    private DoubleProperty currentHealth;


    @FXML
    private Label swordLabel;
    @FXML
    private Label helmetLabel;
    @FXML
    private Label chestplateLabel;
    @FXML
    private Label pantsLabel;
    @FXML
    private Label goldLabel;


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

    private ClientManager clientManager;

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
        clientManager = gameManager.getCm();
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
        gameManager.setGameController(this);
        renderer.start();

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
        Hero localHero = clientManager.getLocalHero();
        maxHealth = new SimpleDoubleProperty(localHero.getMaxHP());
        currentHealth = new SimpleDoubleProperty(localHero.getHp());
        healthBar.progressProperty().bind(currentHealth.divide(maxHealth));
        updateUI();
        log.info("GameController started level systems");
        return true;
    }

    private void updateUI() {
        Hero localHero = clientManager.getLocalHero();
        helmetLabel.setText(localHero.getProfile().getArmor().get(ArmorPieceType.HELMET).getName());
        chestplateLabel.setText(localHero.getProfile().getArmor().get(ArmorPieceType.CHESTPLATE).getName());
        pantsLabel.setText(localHero.getProfile().getArmor().get(ArmorPieceType.PANTS).getName());
        swordLabel.setText(localHero.getProfile().getWeapon().getName());
        maxHealth.set(localHero.getMaxHP());
        currentHealth.set(localHero.getHp());
    }

    public void showChest() {
        currentChest = clientManager.openNearestChest();
        if (currentChest == null) return;
        updateCurChestList();
        chestView.setVisible(true);
    }

    public void hideChest() {
        chestView.setVisible(false);
    }

    public void showInventory() {
        updateInventoryList();
        inventoryView.setVisible(true);
    }

    private void updateInventoryList() {
        Inventory inventory = clientManager.getMyProfile().getInventory();
        ObservableList<Item> items = FXCollections.observableArrayList(inventory.getInvent());
        inventoryList.setItems(items);
    }

    private void updateCurChestList() {
        Inventory inventory = currentChest.getInventory();
        ObservableList<Item> items = FXCollections.observableArrayList(inventory.getInvent());
        chestList.setItems(items);
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
                    } else {
                        useItem(selectedItem);
                    }
                }
            }
        });
    }

    public void useItem(Item selectedItem) {
        if (clientManager.useItem(selectedItem)) {
            updateInventoryList();
            updateUI();
        }
    }

    public void takeItemFromChest(Item selectedItem) {
        clientManager.getMyProfile().getInventory().addItem(selectedItem);
        updateInventoryList();
        currentChest.getInventory().remove(selectedItem);
        updateCurChestList();
    }

    public void storeItemInChest(Item selectedItem) {
        clientManager.getMyProfile().getInventory().remove(selectedItem);
        updateInventoryList();
        currentChest.getInventory().addItem(selectedItem);
        updateCurChestList();
    }

    public void updateHeathBar(int currHP, int maxHP) {
        maxHealth.set(maxHP);
        currentHealth.set(currHP);
    }

    public void setGold(int gold) {
        goldLabel.setText("Gold: " + gold);
    }

}
