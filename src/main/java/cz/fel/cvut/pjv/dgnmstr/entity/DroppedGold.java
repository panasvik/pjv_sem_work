package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class DroppedGold extends Entity {
    private int value;
    public DroppedGold(double positionX, double positionY, Level level, int value) {
        super(1, 0, positionX, positionY, Faction.NEUTRAL, level);
        this.value = value;
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {

    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }

    public int getValue() {
        return value;
    }
}
