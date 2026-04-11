package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class Zombie extends MeleeMob{

    public Zombie(double positionX, double positionY, Level level, int multiplicator, int id) {
        super(MobStats.ZOMBIE.getHp(level.getDifficulty().getStatsMult()),
              MobStats.ZOMBIE.getSpeed(),
              positionX,
              positionY,
              MobStats.ZOMBIE.getDamage(level.getDifficulty().getStatsMult()),
              MobStats.ZOMBIE.getReach(),
              MobStats.ZOMBIE.getGold(),
              Faction.MONSTER,
              level, multiplicator, id);
    }
    public Zombie(double positionX, double positionY, Level level, int id) {
        super(MobStats.ZOMBIE.getHp(level.getDifficulty().getStatsMult()),
                MobStats.ZOMBIE.getSpeed(),
                positionX,
                positionY,
                MobStats.ZOMBIE.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.ZOMBIE.getReach(),
                MobStats.ZOMBIE.getGold(),
                Faction.MONSTER,
                level, 1, id);
    }
}
