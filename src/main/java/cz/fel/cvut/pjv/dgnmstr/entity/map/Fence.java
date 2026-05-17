package cz.fel.cvut.pjv.dgnmstr.entity.map;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.Stats;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;
/**
 * Fence.
 */
public class Fence extends Entity {

    /**
     * Creates a new Fence.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     */
    public Fence(int id,double positionX, double positionY, Level level) {
        super(id, Stats.FENCE.getHealth(), Stats.FENCE.getSpeed(), positionX, positionY, Stats.FENCE.getHalfHitBox(), Faction.NEUTRAL, level);
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        // does nothing
    }

    @Override
    protected int applyDefence(int damage) {
        return damage;
    }

    public boolean isSolid() {
        return true;
    }

    @Override
    public ImgId getImgId() {
        return ImgId.FENCE;
    }


    @Override
    public void update(double deltaTime) {

    }

}
