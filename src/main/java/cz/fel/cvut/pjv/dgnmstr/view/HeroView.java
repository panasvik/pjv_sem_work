package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.item.armor.ArmorPiece;
import cz.fel.cvut.pjv.dgnmstr.item.weapon.Weapon;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;

public class HeroView {
    public Parent build(PlayerProfile profile) {
        StackPane root = new StackPane();
        root.getStyleClass().add("hero-view-background");
        root.getStylesheets().add(getClass().getResource("/cz/fel/cvut/pjv/dgnmstr/view/style.css").toExternalForm());
        HBox overlay = new HBox();
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        Label role = new Label(profile.getRole().toString());
        role.getStyleClass().add("hero-title");
        Label gold = new Label("GOLD: " + profile.getGold());
        gold.getStyleClass().add("gold-label");
        titleBox.getChildren().addAll(role, gold);
        Region leftPart = new Region();
        leftPart.prefWidthProperty().bind(overlay.widthProperty().divide(3));

        HBox rightPart = new HBox(30);
        HBox.setHgrow(rightPart, Priority.ALWAYS);
        rightPart.getStyleClass().add("stats-panel");
        HBox.setMargin(rightPart, new Insets(150, 30, 20, 100));
        rightPart.setPadding(new Insets(30, 30, 30, 30));

        VBox leftCol = new VBox(10);
        HBox.setHgrow(leftCol, Priority.ALWAYS);


        Region space = new Region();
        space.setMinHeight(40);

        VBox rightCol = new VBox(10);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        refreshHeroStats(profile, leftCol, rightCol, gold);
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> SceneManager.getInstance().activate(View.START_MENU));

        rightPart.getChildren().addAll(leftCol, rightCol);
        overlay.getChildren().addAll(leftPart, rightPart);
        titleBox.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20));
        StackPane.setAlignment(titleBox, Pos.TOP_CENTER);
        StackPane.setMargin(titleBox, new Insets(30, 0, 0, 0));

        VBox inventory = createInventoryVBox();
        initInventoryListView(profile, inventory, () -> refreshHeroStats(profile, leftCol, rightCol, gold));

        StackPane.setAlignment(inventory, Pos.TOP_RIGHT);
        StackPane.setMargin(inventory, new Insets(20, 20, 0, 0));

        root.getChildren().addAll(overlay, titleBox, backButton, inventory);
        return root;
    }

    private VBox createInventoryVBox() {
        VBox inventory = new VBox(10);
        inventory.setAlignment(Pos.TOP_CENTER);
        inventory.setMaxWidth(200);
        inventory.setMaxHeight(300);
        inventory.getStyleClass().add("fantasy-panel");

        Label title = new Label("INVENTORY");
        title.getStyleClass().add("panel-header");
        inventory.getChildren().add(title);
        return inventory;
    }

    private void refreshHeroStats(PlayerProfile profile, VBox leftCol, VBox rightCol, Label gold) {
        gold.setText("GOLD: " + profile.getGold());
        leftCol.getChildren().clear();
        rightCol.getChildren().clear();
        fillArmorDetails(profile, leftCol);
        fillWeaponAndStats(profile, rightCol);
    }

    private void fillArmorDetails(PlayerProfile profile, VBox leftCol) {
        Label armorIntro = new Label("ARMOR");
        armorIntro.getStyleClass().add("section-header");
        leftCol.getChildren().add(armorIntro);

        for (ArmorPiece piece : profile.getArmor().values()) {
            if (piece != null) {
                Label armorName = new Label("     " + piece.getName() + ":");
                armorName.getStyleClass().add("item-name");
                Label armorStats = new Label("          Defence: " + piece.getDefence() + '\n'
                        + "          Dexterity: " + piece.getDexterity() + '\n'
                        + "          Speed: " + piece.getSpeed() + '\n'
                        + "          HP: " + piece.getHp());
                armorStats.getStyleClass().add("item-stats");
                leftCol.getChildren().addAll(armorName, armorStats);
            } else {
                Label noArmor = new Label("   Empty slot");
                noArmor.getStyleClass().add("item-name");
                leftCol.getChildren().add(noArmor);
            }
        }
    }

    private void fillWeaponAndStats(PlayerProfile profile, VBox rightCol) {
        Label weaponIntro = new Label("WEAPON");
        weaponIntro.getStyleClass().add("section-header");
        rightCol.getChildren().add(weaponIntro);

        Weapon weapon = profile.getWeapon();
        if (weapon != null){
            Label weaponName = new Label( "     " + weapon.getName() + ":");
            weaponName.getStyleClass().add("item-name");
            Label weaponStats = new Label("          Damage: " + weapon.getDamage() + '\n'
                    + "          Range: " + weapon.getRange() + '\n'
                    + "          Dexterity: " + weapon.getDexterity());
            weaponStats.getStyleClass().add("item-stats");
            rightCol.getChildren().addAll(weaponName, weaponStats);
        } else {
            Label noWeapon = new Label("   Empty slot");
            noWeapon.getStyleClass().add("item-name");
            rightCol.getChildren().add(noWeapon);
        }

        Region space = new Region();
        space.setMinHeight(20);
        rightCol.getChildren().add(space);

        Label tStats = new Label("TOTAL STATS:");
        tStats.getStyleClass().add("section-header");
        rightCol.getChildren().addAll(tStats,
                createStatRow("Damage", profile.getPlayerStats().damage()),
                createStatRow("Defence", profile.getPlayerStats().defence()),
                createStatRow("Speed", profile.getPlayerStats().speed()),
                createStatRow("HP", profile.getPlayerStats().hp()),
                createStatRow("Range", profile.getPlayerStats().range()),
                createStatRow("Dexterity", profile.getPlayerStats().dexterity()));
    }

    private HBox createStatRow(String label, int value) {
        HBox row = new HBox();
        row.setPadding(new Insets(0, 0, 0, 20));

        Label keyLabel = new Label(label + ":");
        keyLabel.getStyleClass().add("item-name");
        keyLabel.setMinWidth(120);

        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("stat-value");
        valueLabel.setMinWidth(80);
        valueLabel.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(keyLabel, valueLabel);
        return row;
    }

    private void initInventoryListView(PlayerProfile profile, VBox invBox, Runnable onItemUsed) {
        ListView<Item> itemList = new ListView<>();
        itemList.setPrefHeight(400);
        Inventory inventory = profile.getInventory();
        InventoryListView viewList = new InventoryListView(itemList, invBox);
        viewList.setInventory(inventory);
        viewList.fillList();
        viewList.updateList();
        invBox.getChildren().add(itemList);
        setupListInteractions(viewList, profile, onItemUsed);
        viewList.show();
    }

    private void setupListInteractions(InventoryListView viewList, PlayerProfile profile, Runnable onItemUsed) {
        ListView<Item> itemListView = viewList.getItemList();
        itemListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Item selectedItem = itemListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    if (selectedItem.acceptUsing(profile)) {
                        viewList.updateList();
                        onItemUsed.run();
                    }
                }
            }
        });
    }
}
