package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.CanAttackMelee;
import cz.fel.cvut.pjv.dgnmstr.entity.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.PlaterStats;
import cz.fel.cvut.pjv.dgnmstr.global.PlayerProfile;
import cz.fel.cvut.pjv.dgnmstr.global.Role;
import cz.fel.cvut.pjv.dgnmstr.item.ArmorPiece;
import cz.fel.cvut.pjv.dgnmstr.item.Weapon;
import java.util.List;

public class Warrior extends Hero implements CanAttackMelee {
    private static final double cos45 = 0.707;
    public Warrior(PlaterStats stats, double posX, double posY, Level level) {
        super(stats.hp(), stats.speed(), posX, posY,
                stats.damage(), stats.dexterity(), stats.defence(), stats.range(),
                Role.WARRIOR, Faction.HERO, level);
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
            }
        }
    }

    @Override
    public void useUltimate() {

    }



}