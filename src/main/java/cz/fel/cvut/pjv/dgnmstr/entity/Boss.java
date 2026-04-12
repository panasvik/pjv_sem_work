package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;
import java.util.logging.Logger;

public abstract class Boss extends Mob {
    protected List<Integer> phaseThresholds;
    private int currPhase = 0;
    private static final Logger log =  Logger.getLogger(Boss.class.getName());

    public Boss(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level, List<Integer> phaseThresholds) {
        super(hp, speed, positionX, positionY, damage, reach, gold, faction, level, 1);
        this.phaseThresholds = phaseThresholds;
    }



    @Override
    protected void onDamageTaken(int amount, Entity source) {
        if ((currPhase < this.phaseThresholds.size()) && (this.getHp() <= this.phaseThresholds.get(currPhase))) {
            currPhase += 1;
            log.info("Boss " + this.getClass().getSimpleName() +" entering phase " + currPhase);
            changePhase();
        }

    }

    protected abstract void changePhase();

    public int getCurrPhase() {
        return currPhase;
    }

}
