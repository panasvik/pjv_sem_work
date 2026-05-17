package cz.fel.cvut.pjv.dgnmstr.entity.map;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.Stats;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;

/**
 * Grate.
 */
public class Grate extends Entity {
    /**
     * Creates a new Grate.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     */
    public Grate(int id, double positionX, double positionY, Level level) {
        super(id, Stats.GOD.getHealth(), Stats.GOD.getSpeed(), positionX, positionY, Stats.GOD.getHalfHitBox(), Faction.NEUTRAL, level);
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        // does nothing
    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }

    public boolean isSolid() {
        return true;
    }

    @Override
    public ImgId getImgId() {
        return ImgId.GRATE;
    }


    @Override
    public void update(double deltaTime) {

    }
}
