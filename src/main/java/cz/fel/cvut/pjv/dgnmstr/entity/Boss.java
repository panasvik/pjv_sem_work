package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public abstract class Boss extends Mob {
    public Boss(int hp, int speed, double positionX, double positionY, int damage, int reach, int gold, Faction faction, Level level) {
        super(hp, speed, positionX, positionY, damage, reach, gold, faction, level);
    }
    private int currPhase = 0;

    public int getCurrPhase() {
        return currPhase;
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        if (currPhase == 0 && (this.getHp() <= this.getMaxHP()/2)){
            currPhase = 1;
            changePhase(currPhase);
        }
        if (currPhase == 1  && (this.getHp() <= this.getMaxHP()/4)) {
            currPhase = 2;
            changePhase(currPhase);
        }
    }

    public abstract void changePhase(int newPhase);
}
