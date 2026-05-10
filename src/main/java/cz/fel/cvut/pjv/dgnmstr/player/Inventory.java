package cz.fel.cvut.pjv.dgnmstr.player;


import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.item.AllItems;
import cz.fel.cvut.pjv.dgnmstr.item.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * item manager for PlayerProfile
 */
public class Inventory implements Serializable {
    private final List<Item> invent;
    public static final int MAX_SIZE = 16;
    public static final int MIN_CHEST_SIZE = 4;

    private int size;
    private final static Logger log = Logger.getLogger(Inventory.class.getName());

    public Inventory() {
        this.invent = new ArrayList<>();
        this.size = 0;
    }

    /**
     * adds item to invent if it fits
     * @param item item to add
     * @return true added / false not added
     */
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

    public boolean rejectAddition(Item item) {
        log.warning("Unable to add to inventory");
        return false;
    }


    /**
     * removes item from invent
     * @param item item to remove
     * @return true removed / false not removed
     */
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

    public List<Item> getInvent() {
        return invent;
    }

    public void setInvent(List<Item> loadedItems) {
        this.invent.clear();
        if (loadedItems != null) {
            this.invent.addAll(loadedItems);
            this.size = loadedItems.size();
        }
    }

    public static Inventory getRandomInventory() {
        Inventory inventory = new Inventory();
        Random random = new Random();
        int max = random.nextInt(MAX_SIZE - MIN_CHEST_SIZE) + MIN_CHEST_SIZE;
        List<Item> newInv = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            newInv.add(AllItems.getRandomItemInstance());
        }
        inventory.setInvent(newInv);
        return inventory;
    }
}