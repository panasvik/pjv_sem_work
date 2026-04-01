package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class AnysekTheBehemoth extends Boss{

    public AnysekTheBehemoth(double positionX, double positionY, Level level, int diffMod) {
        super(Stats.ANYSEK_THE_BEHEMOTH.getHp(diffMod),
              Stats.ANYSEK_THE_BEHEMOTH.getSpeed(),
              positionX,
              positionY,
              Stats.ANYSEK_THE_BEHEMOTH.getDamage(diffMod),
              Stats.ANYSEK_THE_BEHEMOTH.getReach(),
              Stats.ANYSEK_THE_BEHEMOTH.getGold(),
              Faction.MONSTER,
              level,
              new int[]{800, 600, 400, 200});
    }

    @Override
    public void changePhase(int newPhase) {

    }

    @Override
    public void attack() {

    }
}
