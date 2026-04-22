package cz.fel.cvut.pjv.dgnmstr.entity.map;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.Stats;

public class Fence extends Entity {

    public Fence(int id,double positionX, double positionY, Level level) {
        super(id, Stats.FENCE.getHealth(), Stats.FENCE.getSpeed(), positionX, positionY, Faction.NEUTRAL, level);
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
    public void update(double deltaTime) {

    }
}
