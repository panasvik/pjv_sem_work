package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.List;
import java.util.Random;

public class DwightTheLeviathan extends Boss{
    private int shotCount = 0;

    private int damageMult = 2;
    private Entity target = null;
    private Random random = new Random();

    private static final double COOLDOWN_TIME = 3.0;
    private static final double DANGEROUS_DISTANCE = 0.33;

    public DwightTheLeviathan(double positionX, double positionY, Level level) {
        super(BossStats.DWIGHT_THE_LEVIATHAN.getHp(level.getDifficulty().getStatsMult()),
                BossStats.DWIGHT_THE_LEVIATHAN.getSpeed(),
                positionX,
                positionY,
                BossStats.DWIGHT_THE_LEVIATHAN.getDamage(level.getDifficulty().getStatsMult()),
                BossStats.DWIGHT_THE_LEVIATHAN.getReach(),
                BossStats.DWIGHT_THE_LEVIATHAN.getGold(),
                Faction.MONSTER,
                level,
                BossStats.DWIGHT_THE_LEVIATHAN.getThreshold());
    }

    @Override
    protected void changePhase() {

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

    @Override
    public void update(double deltaTime){
        if(!isAlive()){return;}

        Hero target = this.findNearestPlayer();
        if (target == null){ return;}

        double range = getReach();
        double distS = MyMath.calculateDistanceSquared(this, target);
        double dist = Math.sqrt(distS);

        double dangerous_dist = range * DANGEROUS_DISTANCE;
        if (dist < dangerous_dist){
            retreatFrom(target, range);
        } else if(dist > range * 1.25) {
            int speed = getSpeed();
            setSpeed(speed/2);
            checkMoveToward(target, deltaTime);
            setSpeed(speed);
        }
        if (dist <= range && attackCooldown <= 0) {
            attack();
            attackCooldown = COOLDOWN_TIME;
        }

        if (attackCooldown > 0) {
            attackCooldown -= deltaTime;
        }
    }

}




