package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
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
    private boolean up = false;
    private boolean down = false;
    private boolean left = false;
    private boolean right = false;

    private boolean toAttack  = false;
    private boolean toUltimate  = false;
    private boolean toInteract =  false;

    private double attackCooldown = 0.0;
    private static final double COOLDOWN_TIME = 0.5;

    private double ultActive = 0.0;
    private static final double ULT_DURATION = 8.0;

    private static final double B_STORM_RADIUS_MULT = 5.0;
    private double bladeStormDamage = 0.0;

    public Warrior(PlayerProfile profile, double posX, double posY, Level level) {
        super(profile, posX, posY, Role.WARRIOR, Faction.HERO, level);
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

    @Override
    public void update(double deltaTime) {
        if (!isAlive()) return;

        updateMovement(deltaTime);
        updateAttack();
        updateUltimate();
        updateInteraction();
        updateBladeStorm(deltaTime);
        updateCooldowns(deltaTime);

    }

    private void updateMovement(double deltaTime) {
        double moveX = 0;
        double moveY = 0;

        if (up) moveY -= 1;
        if (down) moveY += 1;
        if (left) moveX -= 1;
        if (right) moveX += 1;

        double length = Math.sqrt(moveX * moveX + moveY * moveY);

        if (length > 0) {
            moveX /= length;
            moveY /= length;

            double newX = getX() + moveX * getSpeed() * deltaTime;
            double newY = getY() + moveY * getSpeed() * deltaTime;

            if (getLevel().isWalkable(newX, newY)) {
                setPos(newX, newY);
            } else if (getLevel().isWalkable(newX, getY())) {
                setPos(newX, getY());
            } else if (getLevel().isWalkable(getX(), newY)){
                setPos(getX(), newY);
            }
        }
    }

    private void updateAttack() {
        if (toAttack && attackCooldown <= 0){
            attack();
            attackCooldown =  COOLDOWN_TIME;
            toAttack = false;
        }
    }

    private void updateUltimate() {
        if (toUltimate && hitsForUlt >= HFULimit){
            useUltimate();
            ultActive = ULT_DURATION;
            toUltimate = false;
        }
    }

    private void updateInteraction(){
        if (toInteract){
            List <Entity> entitiesAround = getLevel().getEntitiesAround(this, this.getReach());
            for (Entity e : entitiesAround) {
                if (e instanceof Chest){
                    ((Chest) e).onInteract(this);
                    break;
                }

            }
            toInteract = false;
        }
    }

    private void updateBladeStorm(double deltaTime){
        if (ultActive > 0) {
            double damPerSecond = getDamage()/2.0;
            bladeStormDamage += damPerSecond * deltaTime;

            if (bladeStormDamage >= 1.0) {
                int damage = (int) bladeStormDamage;

                double bladeStormRadius = B_STORM_RADIUS_MULT * getReach();
                List<Entity> entitiesAround = getLevel().getEntitiesAround(this, bladeStormRadius);

                for (Entity e : entitiesAround) {
                    if (isEnemy(e)){
                        e.takeDamage(damage, this);
                    }
                }
                bladeStormDamage -= damage;
            }
            ultActive -= deltaTime;
        } else {
            bladeStormDamage = 0.0;
        }
    }

    private void updateCooldowns(double deltaTime) {
        if (attackCooldown > 0){
            attackCooldown -= deltaTime;
        }
    }


    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }


    public void setToAttack(boolean toAttack) {
        this.toAttack = toAttack;
    }

    public void setToUltimate(boolean toUltimate) {
        this.toUltimate = toUltimate;
    }

    public void setToInteract(boolean toInteract) {
        this.toInteract = toInteract;
    }


}