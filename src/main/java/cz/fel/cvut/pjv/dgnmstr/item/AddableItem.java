package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

public abstract class AddableItem extends Item {
    public AddableItem(String name, int price) {
        super(name, price);
    }
    protected AddableItem() {
        super();
    }

    @Override
    public boolean addTo(Inventory inventory) {
        inventory.confirmAddition(this); // Double Dispatch
        return true;
    }

    @Override
    public boolean removeFrom(Inventory inventory) {
        return inventory.confirmRemoval(this);
    }
}
