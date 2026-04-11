package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;
import java.util.Random;

public class EvilWizard extends RangedMob{

    public EvilWizard(double positionX, double positionY, Level level, int id) {
        super(MobStats.EVIL_WIZARD.getHp(level.getDifficulty().getStatsMult()),
              MobStats.EVIL_WIZARD.getSpeed(),
              positionX, positionY,
              MobStats.EVIL_WIZARD.getDamage(level.getDifficulty().getStatsMult()),
              MobStats.EVIL_WIZARD.getReach(),
              MobStats.EVIL_WIZARD.getGold(),
              Faction.MONSTER,
              level, 1, id);
    }
    public EvilWizard(double positionX, double positionY, Level level, int multiplicator, int id) {
        super(MobStats.EVIL_WIZARD.getHp(level.getDifficulty().getStatsMult()),
                MobStats.EVIL_WIZARD.getSpeed(),
                positionX, positionY,
                MobStats.EVIL_WIZARD.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.EVIL_WIZARD.getReach(),
                MobStats.EVIL_WIZARD.getGold(),
                Faction.MONSTER,
                level, multiplicator, id);
    }


    @Override
    protected void fireProjectile(double deltaX, double deltaY){
        getLevel().addProjectile(
                new Projectile(15, getX(), getY(), this, getDamage(), deltaX, deltaY, 15)
        );
    }
}
