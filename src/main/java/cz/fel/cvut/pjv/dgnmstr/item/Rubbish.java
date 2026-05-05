package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

public class Rubbish extends Item{
    public Rubbish() {
        super("Rubbish", 0);
    }

    @Override
    public boolean acceptUsing(PlayerProfile profile) {
        return false;
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
