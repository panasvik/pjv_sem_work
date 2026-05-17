package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;
/**
 * Goblin.
 */
public class Goblin extends MeleeMob {
    /**
     * Creates a new Goblin.
     *
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     * @param multiplicator multiplicator
     */
    public Goblin(int id, double positionX, double positionY, Level level, int multiplicator) {
        super( id,
                MobStats.GOBLIN.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getSpeed(),
                positionX,
                positionY,
                MobStats.GOBLIN.getHalfHitBox(),

                MobStats.GOBLIN.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getReach(),
                MobStats.GOBLIN.getGold(),
                Faction.MONSTER,
                level, multiplicator);
    }
    /**
     * Creates a new Goblin.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     */
    public Goblin(int id, double positionX, double positionY, Level level) {
        super( id,
                MobStats.GOBLIN.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getSpeed(),
                positionX,
                positionY,
                MobStats.GOBLIN.getHalfHitBox(),

                MobStats.GOBLIN.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getReach(),
                MobStats.GOBLIN.getGold(),
                Faction.MONSTER,
                level, 1);
    }

    @Override
    public ImgId getImgId() {
        return ImgId.GOBLIN;
    }
}


