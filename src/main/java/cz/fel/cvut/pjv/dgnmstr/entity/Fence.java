package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class Fence extends Entity {

    public Fence(double positionX, double positionY, Level level) {
        super(Stats.FENCE.getHealth(), Stats.FENCE.getSpeed(), positionX, positionY, Faction.NEUTRAL, level);
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
