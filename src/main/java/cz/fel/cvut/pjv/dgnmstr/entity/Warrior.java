package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;
import cz.fel.cvut.pjv.dgnmstr.entity.Role.*;

import java.util.List;
import java.util.logging.Logger;

public class Warrior extends Hero implements CanAttackMelee {
    private static final double cos45 = 0.707;
    private static final int HFULimit = 5;
    private static final int damageMul = 5;
    private static final int ultRange = 2;
    private int hitsForUlt;
    private static final Logger log = Logger.getLogger(Warrior.class.getName());

    public Warrior(PlayerStats stats, double posX, double posY, Level level) {
        super(stats.hp(), stats.speed(), posX, posY,
                stats.damage(), stats.dexterity(), stats.defence(), stats.range(),
                Role.WARRIOR, Faction.HERO, level);
        this.hitsForUlt = 0;
    }

    /**
     * what to do when this took damage
     * @param amount how much damage it took
     * @param source who dealt damage
     */
    @Override
    protected void onDamageTaken(int amount, Entity source) {
        this.hitsForUlt = 0;
    }


    @Override
    public boolean isInRange(Entity entity) {
        double deltaX = entity.getX() - getX();
        double deltaY = entity.getY() - getY();
        return getReach() * getReach() > deltaX * deltaX + deltaY * deltaY;
    }

    @Override
    public boolean isInAngle(Entity entity, double curX, double curY) {
        double deltaXE = entity.getX() - getX();
        double deltaYE = entity.getY() - getY();
        double deltaXC = curX - getX();
        double deltaYC = curY - getY();
        double dotProduct = deltaXE * deltaXC + deltaYE * deltaYC;

        if (dotProduct < 0) return false;

        double squaredDot = dotProduct * dotProduct;
        double squaredMagE = deltaXE * deltaXE + deltaYE * deltaYE;
        double squaredMagC = deltaXC * deltaXC + deltaYC * deltaYC;

        return squaredDot / (squaredMagE * squaredMagC) > cos45 * cos45;
    }

    @Override
    public void attack() {
        List<Entity> entitiesAround = getLevel().getEntitiesAround(this, getReach());
        for (Entity e : entitiesAround) {
            if (this.isEnemy(e) && isInAngle(e, getLookX(), getLookY())) {
                e.takeDamage(getDamage(), this);
                hitsForUlt++;
            }
        }
    }

    @Override
    public void useUltimate() {
        if (hitsForUlt < HFULimit) {
            return;
        }
        log.info("Warrior used Ultimate Power");
        List<Entity> entitiesAround = getLevel().getEntitiesAround(this, getReach() * ultRange);
        for (Entity e : entitiesAround) {
            if (this.isEnemy(e) && isInAngle(e, getLookX(), getLookY())) {
                e.takeDamage(getDamage() * damageMul, this);
                hitsForUlt++;
            }
        }
    }



}