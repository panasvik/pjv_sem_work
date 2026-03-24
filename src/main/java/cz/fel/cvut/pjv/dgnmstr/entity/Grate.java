package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class Grate extends Entity {

    public Grate(double positionX, double positionY, Level level) {
        super(Stats.GOD.getHealth(), Stats.GOD.getSpeed(), positionX, positionY, Faction.NEUTRAL, level);
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        // does nothing
    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }
}
