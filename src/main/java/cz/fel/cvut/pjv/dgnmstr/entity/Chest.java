package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Chest extends Entity{
    Inventory inventory;
    private boolean isOpen = false;

    public Chest(double positionX, double positionY, Level level, Inventory inventory) {
        super(Stats.GOD.getHealth(), Stats.GOD.getSpeed(), positionX, positionY, Stats.GOD.getFaction(), level);
        this.inventory = inventory;
    }


    @Override
    protected void onDamageTaken(int amount, Entity source) {
        // nothing
    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }
}
