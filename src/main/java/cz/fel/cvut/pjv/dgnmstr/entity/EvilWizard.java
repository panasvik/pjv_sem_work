package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;
import java.util.Random;

public class EvilWizard extends Mob{
    private Entity target = null;
    Random random = new Random();
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
    public void attack() {
        if (target == null) {
            List<Entity> ents = getLevel().getEntitiesAround(this, getReach());
            target = ents.get(random.nextInt(ents.size()));
        }
        double deltaX = getX() - target.getX();
        double deltaY = getY() - target.getY();
        getLevel().addProjectile(
                new Projectile(getReach() / 4, getX(), getY(), this, getDamage(), deltaX, deltaY, 15)
        );


    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        target = source;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }
}
