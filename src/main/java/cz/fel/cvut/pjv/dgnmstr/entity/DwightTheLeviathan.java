package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;
import java.util.Random;

public class DwightTheLeviathan extends Boss{
    private int shotCount = 0;
    private int coolDown = 8;
    private int damageMult = 2;
    private Entity target = null;
    private Random random = new Random();
    public DwightTheLeviathan(double positionX, double positionY, Level level, int id) {
        super(BossStats.DWIGHT_THE_LEVIATHAN.getHp(level.getDifficulty().getStatsMult()),
                BossStats.DWIGHT_THE_LEVIATHAN.getSpeed(),
                positionX,
                positionY,
                BossStats.DWIGHT_THE_LEVIATHAN.getDamage(level.getDifficulty().getStatsMult()),
                BossStats.DWIGHT_THE_LEVIATHAN.getReach(),
                BossStats.DWIGHT_THE_LEVIATHAN.getGold(),
                Faction.MONSTER,
                level,
                BossStats.DWIGHT_THE_LEVIATHAN.getThreshold(),
                id);
    }

    @Override
    protected void changePhase() {
        coolDown--;
        damageMult++;
    }

    @Override
    public void attack() {
        if (target == null) {
            List<Entity> ents = getLevel().getEntitiesAround(this, getReach());
            if (!ents.isEmpty()) {
                target = ents.get(random.nextInt(ents.size()));
            } else {
                return;
            }
        }
        double deltaX = getX() - target.getX();
        double deltaY = getY() - target.getY();

        shotCount++;
        if (shotCount >= 5){
            int stats = 3 * damageMult;
            getLevel().addProjectile(
                    new Projectile(stats, getX(), getY(), this, getDamage(), deltaX, deltaY, stats));
            getLevel().addProjectile(
                    new Projectile(stats, getX(), getY(), this, getDamage(), deltaX + 25, deltaY + 25, stats));
            getLevel().addProjectile(
                    new Projectile(stats, getX(), getY(), this, getDamage(), deltaX - 25, deltaY - 25, stats)); // Možná optimalizace
            shotCount = 0;

        } else {
            int stats = 5 * damageMult;
            getLevel().addProjectile(
                    new Projectile(stats, getX(), getY(), this, getDamage(), deltaX, deltaY, stats) //Nejspíš bude potřeba zoptimalizovat
            );
        }
    }
}
