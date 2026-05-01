package cz.fel.cvut.pjv.dgnmstr.entity.map;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Interactable;
import cz.fel.cvut.pjv.dgnmstr.entity.Stats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import javafx.scene.image.Image;

public class Chest extends Entity implements Interactable {
    private final Inventory inventory;
    public Chest(int id, double positionX, double positionY, Level level, Inventory inventory) {
        super(id, Stats.GOD.getHealth(), Stats.GOD.getSpeed(), positionX, positionY, Stats.GOD.getFaction(), level);
        this.inventory = inventory;
    }

    @Override
    public void onInteract(Hero player) {

    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        // nothing, cannot take damage
    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }

    public boolean isSolid() {
        return true;
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public void update(double deltaTime) {

    }
}