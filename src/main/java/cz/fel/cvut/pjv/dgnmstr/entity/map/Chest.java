package cz.fel.cvut.pjv.dgnmstr.entity.map;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Interactable;
import cz.fel.cvut.pjv.dgnmstr.entity.Stats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

import java.util.logging.Logger;

public class Chest extends Entity implements Interactable {
    private static final Logger log =  Logger.getLogger(Chest.class.getName());

    public static final double MAX_DIST_TO_OPEN = 2500; // already squared value
    public static final int PRICE_TO_OPEN_CHEST = 100;
    private final Inventory inventory;
    private boolean opened = false;
    public Chest(int id, double positionX, double positionY, Level level, Inventory inventory) {
        super(id, Stats.GOD.getHealth(), Stats.GOD.getSpeed(), positionX, positionY, Stats.GOD.getFaction(), level);
        this.inventory = inventory;
    }

    @Override
    public boolean interact(Hero hero) {
        int currGold = hero.getGold();
        if (opened) return true;
        if (currGold > PRICE_TO_OPEN_CHEST) {
            opened = true;
            hero.setGold(currGold - PRICE_TO_OPEN_CHEST);
        } else {
            log.info("not enough gold to open " + currGold);
        }
        return opened;
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        // nothing, cannot take damage
    }

    @Override
    protected int applyDefence(int damage) {
        return 0; //invincible
    }

    public boolean isSolid() {
        return true;
    }

    @Override
    public ImgId getImgId() {
        return ImgId.CHEST;
    }

    @Override
    public void update(double deltaTime) {

    }

    public Inventory getInventory() {
        return inventory;
    }
}