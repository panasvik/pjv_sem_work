package cz.fel.cvut.pjv.dgnmstr.entity.map;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import javafx.scene.image.Image;

import java.util.logging.Logger;

/**
 * dropped gold after a mob dies
 */
public class DroppedGold extends Entity {
    private int value;
    private static final double PICKUP_RADIUS = Level.TILE_SIZE / 2.0;
    public DroppedGold(int id, double positionX, double positionY, Level level, int value) {
        super(id, 1, 0, positionX, positionY, Faction.NEUTRAL, level);
        this.value = value;
    }
    private static final Logger log =  Logger.getLogger(DroppedGold.class.getName());


    @Override
    public void update(double deltaTime) {
        if (!isAlive()) return;

        for (Hero player : getLevel().getPlayers()) {
            if (player.isAlive()) {
                double distSquared = MyMath.calculateDistanceSquared(this, player);

                if (distSquared <= (PICKUP_RADIUS * PICKUP_RADIUS)) {
                    player.addGold(value);
                    log.fine("Gold taken: " + value + ". OverAll: " + player.getGold());

                    this.die();
                    break;
                }
            }
        }
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {

    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }

    @Override
    public Image getImage() {
        return null;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void acceptUpdateEjection(ServerNetworkManager serverNetworkManager) {

    }
}
