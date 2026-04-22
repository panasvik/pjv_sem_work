package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.Projectile;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;

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
        int id = GameManager.getInstance().getIdGenerator().getNextId();
        getLevel().addProjectile(
                new Projectile(id, 5, getX(), getY(), this, getDamage(), deltaX, deltaY, 25)
        );
    }

    @Override
    public void update(double deltaTime) {

    }
}


