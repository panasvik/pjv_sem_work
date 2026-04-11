package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

public class Chest extends Entity implements Interactable {
    private final Inventory inventory;
    public Chest(double positionX, double positionY, Level level, Inventory inventory, int id) {
        super(Stats.GOD.getHealth(), Stats.GOD.getSpeed(), positionX, positionY, Stats.GOD.getFaction(), level, id);
        this.inventory = inventory;
    }

    @Override
    public void onInteract(Hero player) {
        //TODO
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        // nothing
    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }

    public boolean isSolid() {
        return true;
    }
}