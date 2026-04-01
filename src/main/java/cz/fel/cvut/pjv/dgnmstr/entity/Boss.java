package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public abstract class Boss extends Mob {
    protected int[] phaseThresholds;
    public Boss(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, int[] phaseThresholds) {
        super(hp, speed, positionX, positionY, damage, reach, gold, faction, level);
        this.phaseThresholds = phaseThresholds;

    }

    private int currPhase = 0;

    public int getCurrPhase() {
        return currPhase;
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        if ((currPhase < this.phaseThresholds.length) && (this.getHp() <= this.phaseThresholds[currPhase])) {
            currPhase += 1;
            changePhase(currPhase);
        }

    }

    public abstract void changePhase(int newPhase);
}
