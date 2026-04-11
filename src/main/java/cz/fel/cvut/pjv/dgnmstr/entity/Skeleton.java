package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.Random;

public class Skeleton extends RangedMob{

    public Skeleton(double positionX, double positionY, Level level, int id) {
        super(MobStats.SKELETON.getHp(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getSpeed(),
                positionX, positionY,
                MobStats.SKELETON.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getReach(),
                MobStats.SKELETON.getGold(),
                Faction.MONSTER,
                level, 1, id);
    }
    public Skeleton(double positionX, double positionY, Level level, int multiplicator, int id) {
        super(MobStats.SKELETON.getHp(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getSpeed(),
                positionX, positionY,
                MobStats.SKELETON.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.SKELETON.getReach(),
                MobStats.SKELETON.getGold(),
                Faction.MONSTER,
                level, multiplicator, id);
    }

    @Override
    protected void fireProjectile(double deltaX, double deltaY){
        getLevel().addProjectile(
                new Projectile(5, getX(), getY(), this, getDamage(), deltaX, deltaY, 25)
        );
    }
}


