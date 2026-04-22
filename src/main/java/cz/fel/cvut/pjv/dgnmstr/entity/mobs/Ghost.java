package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;


public class Ghost extends MeleeMob{
    public Ghost(int id, double positionX, double positionY, Level level) {
        super( id,
                MobStats.GHOST.getHp(level.getDifficulty().getStatsMult()),
              MobStats.GHOST.getSpeed(),
              positionX,
              positionY,
              MobStats.GHOST.getDamage(level.getDifficulty().getStatsMult()),
              MobStats.GHOST.getReach(),
              MobStats.GHOST.getGold(),
              Faction.MONSTER,
              level, 1);
    }

    public Ghost(int id, double positionX, double positionY, Level level, int multiplicator) {
        super( id,
                MobStats.GHOST.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GHOST.getSpeed(),
                positionX,
                positionY,
                MobStats.GHOST.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.GHOST.getReach(),
                MobStats.GHOST.getGold(),
                Faction.MONSTER,
                level, multiplicator);
    }

    @Override
    public boolean isEthereal() {
        return true;
    }

    @Override
    protected double attackAngle(){
        return Math.PI/2;
    }


    @Override
    public void update(double deltaTime) {

    }
}

