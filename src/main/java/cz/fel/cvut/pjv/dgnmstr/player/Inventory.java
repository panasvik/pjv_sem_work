package cz.fel.cvut.pjv.dgnmstr.player;


import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Inventory {
    private final List<AddableItem> invent;
    private static final int MAX_SIZE = 16;
    private int size;
    private final static Logger log = Logger.getLogger(Inventory.class.getName());

    public Inventory() {
        this.invent = new ArrayList<>();
        this.size = 0;
    }

    public boolean addItem(Item item) {
        if (size >= MAX_SIZE) {
            log.info("Not enough space");
            return false;
        }
        return item.addTo(this);
    }

    public void confirmAddition(AddableItem addableItem) {
        this.invent.add(addableItem);
        this.size++;
        log.fine("Added " + addableItem + " to inventory");
    }

    public void rejectAddition(Item item) {
        log.warning("Unable to add to inventory");
    }


    public boolean remove(Item item) {
        return item.removeFrom(this);
    }

    public boolean confirmRemoval(AddableItem addableItem) {
        boolean ret = invent.remove(addableItem);
        if (ret) {
            size--;
            log.fine("Removed " + addableItem + " from inventory");
        }
        return ret;
    }

    public boolean rejectRemoval(Item item) {
        log.info("Unremovable item");
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