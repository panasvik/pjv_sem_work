package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.ProjectileType;

/**
 * Evil wizard.
 */
public class EvilWizard extends RangedMob{
    /**
     * Creates a new EvilWizard.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     */
    public EvilWizard(int id, double positionX, double positionY, Level level) {
        super(id, MobStats.EVIL_WIZARD.getHp(level.getDifficulty().getStatsMult()),
              MobStats.EVIL_WIZARD.getSpeed(),
              positionX, positionY,
              MobStats.EVIL_WIZARD.getHalfHitBox(),
              MobStats.EVIL_WIZARD.getDamage(level.getDifficulty().getStatsMult()),
              MobStats.EVIL_WIZARD.getReach(),
              MobStats.EVIL_WIZARD.getGold(),
              Faction.MONSTER,
              level, 1);
    }
    /**
     * Creates a new EvilWizard.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     * @param multiplicator multiplicator
     */
    public EvilWizard(int id, double positionX, double positionY, Level level, int multiplicator) {
        super(id, MobStats.EVIL_WIZARD.getHp(level.getDifficulty().getStatsMult()),
                MobStats.EVIL_WIZARD.getSpeed(),
                positionX, positionY,
                MobStats.EVIL_WIZARD.getHalfHitBox(),

                MobStats.EVIL_WIZARD.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.EVIL_WIZARD.getReach(),
                MobStats.EVIL_WIZARD.getGold(),
                Faction.MONSTER,
                level, multiplicator    );
    }


    @Override
    protected void fireProjectile(double deltaX, double deltaY){
        getLevel().requestProjectileSpawn(ProjectileType.FIREBALL,this, 15, getDamage(), deltaX, deltaY, 10);
    }



    @Override
    public ImgId getImgId() {
        return ImgId.EVIL_WIZARD;
    }
}
