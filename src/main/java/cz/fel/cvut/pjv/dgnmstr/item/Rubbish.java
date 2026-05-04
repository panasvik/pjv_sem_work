package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

public class Rubbish extends Item{
    public Rubbish() {
        super("Rubbish", 0);
    }

    @Override
    public boolean addTo(Inventory inventory) {
        return inventory.rejectAddition(this);
    }

    @Override
    public boolean removeFrom(Inventory inventory) {
        return  inventory.rejectRemoval(this);
    }
}
