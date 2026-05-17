package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;


/**
 * Ghost.
 */
public class Ghost extends MeleeMob{
    /**
     * Creates a new Ghost.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     */
    public Ghost(int id, double positionX, double positionY, Level level) {
        super( id,
              MobStats.GHOST.getHp(level.getDifficulty().getStatsMult()),
              MobStats.GHOST.getSpeed(),
              positionX,
              positionY,
              MobStats.GHOST.getHalfHitBox(),
              MobStats.GHOST.getDamage(level.getDifficulty().getStatsMult()),
              MobStats.GHOST.getReach(),
              MobStats.GHOST.getGold(),
              Faction.MONSTER,
              level, 1);
    }

    /**
     * Creates a new Ghost.
     *
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     * @param multiplicator multiplicator
     */
    public Ghost(int id, double positionX, double positionY, Level level, int multiplicator) {
        super( id,
                MobStats.GHOST.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GHOST.getSpeed(),
                positionX,
                positionY,
                MobStats.GHOST.getHalfHitBox(),
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
    public ImgId getImgId() {
        return ImgId.GHOST;
    }

    @Override
    protected double attackAngle(){
        return Math.PI * 2;
    }



}

