package cz.fel.cvut.pjv.dgnmstr.entity.boss;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.ProjectileType;

import java.util.Random;

public class DwightTheLeviathan extends Boss{
    private int shotCount = 0;

    private int damageMult = 2;
    private Hero target = null;


    private static final double COOLDOWN_TIME = 1.0;
    private static final double DANGEROUS_DISTANCE = 0.5;

    public DwightTheLeviathan(int id, double positionX, double positionY, Level level) {
        super(id, BossStats.DWIGHT_THE_LEVIATHAN.getHp(level.getDifficulty().getStatsMult()),
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
        if (target == null || !target.isAlive()) {
            target = findNearestPlayer();
             if (target == null){
                return;
            }
        }
        double deltaX = target.getCenterX() - getCenterX();
        double deltaY = target.getCenterY() - getCenterY();

        shotCount++;
        if (shotCount >= 5){
            int stats = 3 * damageMult;
            double defAngle = Math.atan2(deltaY, deltaX);
            double offset = Math.toRadians(20);
            double [] angles = {defAngle - offset, defAngle, defAngle + offset};
            for (double angle : angles) {
                double dx = Math.cos(angle);
                double dy = Math.sin(angle);
                getLevel().requestProjectileSpawn(ProjectileType.WATERBALL, this, stats, getDamage(), dx, dy, stats);
            }
            shotCount = 0;

        } else {
            int stats = 5 * damageMult;
            getLevel().requestProjectileSpawn(ProjectileType.WATERBALL,this, stats, getDamage(), deltaX, deltaY, stats);

        }
    }

    @Override
    public void update(double deltaTime){
        if(!isAlive()){return;}

        Hero target = this.findNearestPlayer();
        if (target == null){ return;}

        double range = getReach();
        double dx = target.getCenterX() - this.getCenterX();
        double dy = target.getCenterY() - this.getCenterY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        double dangerous_dist = range * DANGEROUS_DISTANCE;
        if (dist < dangerous_dist){
            retreatFrom(target);
        } else if(dist > range * 1.25) {
            int speed = getSpeed();
            setSpeed(speed/2);
            checkMoveToward(target);
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

    @Override
    public ImgId getImgId() {
        return ImgId.DWIGHT;
    }
}




