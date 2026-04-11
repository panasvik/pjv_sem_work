package cz.fel.cvut.pjv.dgnmstr.player;


import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final List<AddableItem> invent;
    private static final int MAX_SIZE = 16;
    private int size;

    public Inventory() {
        this.invent = new ArrayList<>();
        this.size = 0;
    }

    public boolean addItem(Item item) {
        if (size >= MAX_SIZE) {
            System.out.println("Not enough space");
            return false;
        }
        return item.addTo(this);
    }

    public void confirmAddition(AddableItem addableItem) {
        this.invent.add(addableItem);
    }

    public void rejectAddition(Item item) {
        System.out.println("Unable to add to inventory");
    }


    public boolean remove(Item item) {
        return item.removeFrom(this);
    }

    public boolean confirmRemoval(AddableItem addableItem) {
        boolean ret = invent.remove(addableItem);
        if (ret) {
            size--;
        }
        return ret;
    }

    public boolean rejectRemoval(Item item) {
        System.out.println("Unremovable item");
        return false;
    }

    public List<AddableItem> getInvent() {
        return invent;
    }

    public void setInvent(List<AddableItem> loadedItems) {
        this.invent.clear();
        if (loadedItems != null) {
            this.invent.addAll(loadedItems);
            this.size = loadedItems.size();
        }
    }
}