package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class Zombie extends MeleeMob{

    public Zombie(double positionX, double positionY, Level level, double diffMod) {

        super(Stats.ZOMBIE.getHp(diffMod),
              Stats.ZOMBIE.getSpeed(),
              positionX,
              positionY,
              Stats.ZOMBIE.getDamage(diffMod),
              Stats.ZOMBIE.getReach(),
              Stats.ZOMBIE.getGold(),
              Faction.MONSTER,
              level);
    }
}
