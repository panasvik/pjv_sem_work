package cz.fel.cvut.pjv.dgnmstr.view;


import cz.fel.cvut.pjv.dgnmstr.dungeon.crafting.AllRecipes;
import cz.fel.cvut.pjv.dgnmstr.dungeon.crafting.Crafter;
import cz.fel.cvut.pjv.dgnmstr.dungeon.crafting.Recipe;
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
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.logging.Logger;


/**
 * JavaFX controller for the in-level screen. Wires keyboard
 * input and the inventory/chest/crafting panels to game logic.
 */
public class GameController implements Reappearance {
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

    @FXML
    private VBox tutorialBox;

    @FXML
    private Label tutorialName;

    @FXML
    private Label tutorialMessage;

    @FXML
    private VBox craftingView;

    @FXML
    private ListView<Recipe> recipeList;

    private InventoryListView heroInventory;
    private InventoryListView chestInventory;

    private final TutorialText[] tutorialValues = TutorialText.values();
    private int currentTutorialIndex = 0;

    private GameRenderer renderer;
    private EventHandler<KeyEvent> menuToggleHandler;



    public GameController() {
        this.gameManager = GameManager.getInstance();
    }

    private ClientManager clientManager;

    private final Crafter crafter = new Crafter();

    @FXML
    public void initialize() {
        gameCanvas.widthProperty().bind(canvasWrapper.widthProperty());
        gameCanvas.heightProperty().bind(canvasWrapper.heightProperty());
        ImgLoader.loadAll();
        tutorialBox.setVisible(true);
        tutorialMessage.setWrapText(true);
        tutorialName.setWrapText(true);
        updateTutorialUI();
    }

    @Override
    public void reappear() {
        startWhenClientStateReady();
        heroInventory = new InventoryListView(inventoryList, inventoryView);
        heroInventory.setInventory(gameManager.getClientManager().getMyProfile().getInventory());
        heroInventory.fillList();
        heroInventory.updateList();
        chestInventory = new InventoryListView(chestList, chestView);
        chestInventory.fillList();
        setupListInteractions();
        fillRecipeList();
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
        clientManager = gameManager.getClientManager();
        ClientNetworkManager cnm = gameManager.getClientNetworkManager();
        if (cnm == null || clientManager == null || clientManager.getLc() == null || clientManager.getLocalHero() == null) {
            return false;
        }

        if (renderer != null) {
            log.info("starting");
            return true;
        }

        InputHandler inputHandler = new InputHandler(SceneManager.getInstance().getMainScene());
        clientManager.setInputHandler(inputHandler);
        gameManager.setInputHandler(inputHandler);
        renderer = new GameRenderer(gameCanvas, clientManager.getLc().getLevel(), clientManager.getLocalHero());
        gameManager.setGameRenderer(renderer);
        gameManager.setGameController(this);
        renderer.start();

        installMenuToggleHandler();

        clientManager.startManagingInputs();
        Hero localHero = clientManager.getLocalHero();
        maxHealth = new SimpleDoubleProperty(localHero.getMaxHP());
        currentHealth = new SimpleDoubleProperty(localHero.getHp());
        healthBar.progressProperty().bind(currentHealth.divide(maxHealth));
        updateUI();
        log.info("GameController started level systems");
        return true;
    }

    private void installMenuToggleHandler() {
        if (menuToggleHandler != null) {
            return;
        }
        menuToggleHandler = event -> {
            if (event.getCode() == KeyCode.I) {
                heroInventory.toggle();
            } else if (event.getCode() == KeyCode.F) {
                if (chestView.isVisible()) {
                    chestInventory.hide();
                } else {
                    showChest();
                }
            } else if (event.getCode() == KeyCode.C) {
                if (craftingView.isVisible()) {
                    hideCrafting();
                } else {
                    showCrafting();
                }
            }
        };
        SceneManager.getInstance().getMainScene().addEventFilter(KeyEvent.KEY_PRESSED, menuToggleHandler);
    }

    private void fillRecipeList() {
        recipeList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                if (empty || recipe == null) {
                    setText(null);
                    setStyle("");
                } else {
                    StringBuilder sb = new StringBuilder(recipe.getName());
                    sb.append("  (");
                    boolean first = true;
                    for (Map.Entry<Class<? extends Item>, Integer> e : recipe.getIngredients().entrySet()) {
                        if (!first) sb.append(", ");
                        sb.append(e.getValue()).append(" ").append(e.getKey().getSimpleName());
                        first = false;
                    }
                    sb.append(")");
                    setText(sb.toString());

                    boolean canCraft = recipe.isCraftable(clientManager.getMyProfile());
                    setStyle(canCraft ? "" : "-fx-opacity: 0.4;");
                }
            }
        });
    }

    public void stopGameSystems() {
        if (renderer != null) {
            renderer.stop();
            renderer = null;
        }
        if (menuToggleHandler != null) {
            SceneManager.getInstance().getMainScene().removeEventFilter(KeyEvent.KEY_PRESSED, menuToggleHandler);
            menuToggleHandler = null;
        }
        if (healthBar != null) {
            healthBar.progressProperty().unbind();
        }
        heroInventory.hide();
        chestInventory.hide();
        currentChest = null;
        closeTutorial();
    }

    private void updateUI() {
        Hero localHero = clientManager.getLocalHero();
        helmetLabel.setText(localHero.getProfile().getArmor().get(ArmorPieceType.HELMET).getName());
        chestplateLabel.setText(localHero.getProfile().getArmor().get(ArmorPieceType.CHESTPLATE).getName());
        pantsLabel.setText(localHero.getProfile().getArmor().get(ArmorPieceType.PANTS).getName());
        swordLabel.setText(localHero.getProfile().getWeapon().getName());
        maxHealth.set(localHero.getMaxHP());
        currentHealth.set(localHero.getHp());
        heroInventory.updateList();
        if (currentChest == null) {
            return;
        }
        chestInventory.updateList(currentChest.getInventory());
    }

    public void showChest() {
        currentChest = clientManager.openNearestChest();
        if (currentChest == null) return;
        chestInventory.updateList(currentChest.getInventory());
        chestView.setVisible(true);
    }

    public void showCrafting() {
        updateRecipeList();
        craftingView.setVisible(true);
    }

    public void hideCrafting() {
        craftingView.setVisible(false);
    }

    private void updateRecipeList() {
        ObservableList<Recipe> recipes = FXCollections.observableArrayList(AllRecipes.allRecipes());
        recipeList.setItems(recipes);
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

        recipeList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Recipe selected = recipeList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    craftRecipe(selected);
                }
            }
        });
    }

    public void useItem(Item selectedItem) {
        if (clientManager.useItem(selectedItem)) {
            updateUI();
        }
    }

    public void takeItemFromChest(Item selectedItem) {
        clientManager.getMyProfile().getInventory().addItem(selectedItem);
        heroInventory.updateList();
        currentChest.getInventory().remove(selectedItem);
        chestInventory.updateList(currentChest.getInventory());
    }

    public void storeItemInChest(Item selectedItem) {
        clientManager.getMyProfile().getInventory().remove(selectedItem);
        heroInventory.updateList();
        currentChest.getInventory().addItem(selectedItem);
        chestInventory.updateList(currentChest.getInventory());
    }

    public void craftRecipe(Recipe recipe) {
        PlayerProfile profile = clientManager.getMyProfile();
        if (crafter.craft(recipe, profile)) {
            clientManager.getNetworkManager().sendProfileUpdate(profile);
            updateRecipeList();
            heroInventory.updateList();
        }
    }

    public void updateHealthBar(int currHP, int maxHP) {
        maxHealth.set(maxHP);
        currentHealth.set(currHP);
    }

    public void setGold(int gold) {
        goldLabel.setText("Gold: " + gold);
    }


    @FXML
    void nextMessage() {
        currentTutorialIndex++;

        if (currentTutorialIndex < tutorialValues.length) {
            updateTutorialUI();
        } else {
            closeTutorial();
        }
    }

    @FXML
    void closeTutorial() {
        tutorialBox.setVisible(false);
        currentTutorialIndex = 0;
    }

    private void updateTutorialUI() {
        TutorialText current = tutorialValues[currentTutorialIndex];
        tutorialName.setText(current.getName());
        tutorialMessage.setText(current.getMessage());
    }

}
