package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class InventoryListView {
    private final ListView<Item> itemList;
    private final VBox view;
    private Inventory inventory;


    public InventoryListView(ListView<Item> itemList, VBox view) {
        this.itemList = itemList;
        this.view = view;
    }

    public void updateList(Inventory inventory) {
        if (inventory == null) return;
        ObservableList<Item> items = FXCollections.observableArrayList(inventory.getInvent());
        itemList.setItems(items);
    }

    public void updateList() {
        if (inventory == null) return;
        ObservableList<Item> items = FXCollections.observableArrayList(inventory.getInvent());
        itemList.setItems(items);
    }

    public void fillList() {
        itemList.setCellFactory(i -> new ListCell<>() {
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

    public void show() {
        view.setVisible(true);
    }

    public void hide() {
        view.setVisible(false);
    }

    public void toggle() {
        if (view.isVisible()) {
            hide();
        } else {
            show();
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public ListView<Item> getItemList() {
        return itemList;
    }
}
