package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;

public abstract class Boss extends Mob {
    protected List<Integer> phaseThresholds;
    private int currPhase = 0;

    public Boss(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, List<Integer> phaseThresholds) {
        super(hp, speed, positionX, positionY, damage, reach, gold, faction, level);
        this.phaseThresholds = phaseThresholds;
    }



    @Override
    protected void onDamageTaken(int amount, Entity source) {
        if ((currPhase < this.phaseThresholds.size()) && (this.getHp() <= this.phaseThresholds.get(currPhase))) {
            currPhase += 1;
            changePhase();
        }

    }

    protected abstract void changePhase();

    public int getCurrPhase() {
        return currPhase;
    }

}
