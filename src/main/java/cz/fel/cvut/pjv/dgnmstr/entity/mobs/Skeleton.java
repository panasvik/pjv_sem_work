package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.ProjectileType;

public class Skeleton extends RangedMob{
    public Skeleton(int id, double positionX, double positionY, Level level) {
        super(id,MobStats.SKELETON.getHp(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getSpeed(),
                positionX, positionY,
                MobStats.SKELETON.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getReach(),
                MobStats.SKELETON.getGold(),
                Faction.MONSTER,
                level, 1);
    }
    public Skeleton(int id, double positionX, double positionY, Level level, int multiplicator) {
        super(id, MobStats.SKELETON.getHp(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getSpeed(),
                positionX, positionY,
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


