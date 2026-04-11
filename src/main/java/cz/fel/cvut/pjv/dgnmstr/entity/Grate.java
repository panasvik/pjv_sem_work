package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class Grate extends Entity {

    public Grate(double positionX, double positionY, Level level, int id) {
        super(Stats.GOD.getHealth(), Stats.GOD.getSpeed(), positionX, positionY, Faction.NEUTRAL, level, id);
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
}
