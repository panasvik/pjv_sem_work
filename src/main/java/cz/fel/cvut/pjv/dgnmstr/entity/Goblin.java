package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public class Goblin extends MeleeMob {
    public Goblin(double positionX, double positionY, Level level, int multiplicator) {
        super(MobStats.GOBLIN.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getSpeed(),
                positionX,
                positionY,
                MobStats.GOBLIN.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getReach(),
                MobStats.GOBLIN.getGold(),
                Faction.MONSTER,
                level, multiplicator);
    }
    public Goblin(double positionX, double positionY, Level level) {
        super(MobStats.GOBLIN.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getSpeed(),
                positionX,
                positionY,
                MobStats.GOBLIN.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getReach(),
                MobStats.GOBLIN.getGold(),
                Faction.MONSTER,
                level, 1);
    }

    @Override
    public void update(double deltaTime) {

    }
}


