package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.ProjectileType;
/**
 * Skeleton.
 */
public class Skeleton extends RangedMob{
    /**
     * Creates a new Skeleton.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     */
    public Skeleton(int id, double positionX, double positionY, Level level) {
        super(id,MobStats.SKELETON.getHp(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getSpeed(),
                positionX, positionY,
                MobStats.SKELETON.getHalfHitBox(),
                MobStats.SKELETON.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getReach(),
                MobStats.SKELETON.getGold(),
                Faction.MONSTER,
                level, 1);
    }
    /**
     * Creates a new Skeleton.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     * @param multiplicator multiplicator
     */
    public Skeleton(int id, double positionX, double positionY, Level level, int multiplicator) {
        super(id, MobStats.SKELETON.getHp(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getSpeed(),
                positionX, positionY,
                MobStats.SKELETON.getHalfHitBox(),
                MobStats.SKELETON.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getReach(),
                MobStats.SKELETON.getGold(),
                Faction.MONSTER,
                level, multiplicator);
    }

    @Override
    protected void fireProjectile(double deltaX, double deltaY){
        getLevel().requestProjectileSpawn(ProjectileType.ARROW, this, 5, getDamage(), deltaX, deltaY, 15);
    }



    @Override
    public ImgId getImgId() {
        return ImgId.SKELETON;
    }
}


