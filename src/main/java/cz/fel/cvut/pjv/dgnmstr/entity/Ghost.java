package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;


public class Ghost extends MeleeMob{
    public Ghost(double positionX, double positionY, Level level, double diffMod) {
        super(Stats.GHOST.getHp(diffMod),
              Stats.GHOST.getSpeed(),
              positionX,
              positionY,
              Stats.GHOST.getDamage(diffMod),
              Stats.GHOST.getReach(),
              Stats.GHOST.getGold(),
              Faction.MONSTER,
              level);
    }

    @Override
    public boolean isEthereal() {
        return true;
    }

    @Override
    protected double attackAngle(){
        return Math.PI/2;
    }

}

