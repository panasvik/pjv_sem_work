package cz.fel.cvut.pjv.dgnmstr.entity.hero;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanAttackMelee;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.List;
import java.util.logging.Logger;

public class Warrior extends Hero implements CanAttackMelee {
    private static final double cos45 = 0;
    private static final int HFULimit = 5;
    private static final int damageMul = 5;
    private static final int ultRange = 2;
    private int hitsForUlt;
    private static final Logger log = Logger.getLogger(Warrior.class.getName());

    private double attackCooldown = 0.0;
    private static final double COOLDOWN_TIME = 0.2;

    private double ultActive = 0.0;
    private static final double ULT_DURATION = 8.0;

    private static final double B_STORM_RADIUS_MULT = 5.0;
    private double bladeStormDamage = 0.0;

    public Warrior(int id, PlayerProfile profile, double posX, double posY, Level level) {
        super(id, profile, posX, posY, Role.WARRIOR, Faction.HERO, level);
        this.hitsForUlt = 0;
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        this.hitsForUlt = 0;
    }

    @Override
    public ImgId getImgId() {
        return ImgId.WARRIOR;
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
        log.info("attack method activated");
        List<Entity> entitiesAround = getLevel().getEntitiesAround(this, getReach());
        for (Entity e : entitiesAround) {
            if (this.isEnemy(e) && isInAngle(e, getLookX(), getLookY())) {
                log.info("enemy is in range, attacking");
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

        if (currentInputs != null) {
            setLookTarget(currentInputs.mouseX() + getX(), currentInputs.mouseY() + getY());

            updateMovement(deltaTime, currentInputs);
            updateAttack(currentInputs);
            updateUltimate(currentInputs);
            updateInteraction(currentInputs);
//            log.info("player moved : " + currentInputs.down() + " "
//                    + currentInputs.down() + " "
//                    + currentInputs.up() + " "
//                    + currentInputs.left() + " "
//                    + currentInputs.right() + " "
//                    + currentInputs.ult() + " "
//                    + currentInputs.interact() + " "
//                    + currentInputs.attack() + " "
//                    + currentInputs.mouseX() + " "
//                    + currentInputs.mouseY() + " " );
            currentInputs = null;
        }

        updateBladeStorm(deltaTime);
        updateCooldowns(deltaTime);
    }

    private void updateMovement(double deltaTime, Inputs inputs) {
        double moveX = 0;
        double moveY = 0;

        if (inputs.up()) moveY -= 1;
        if (inputs.down()) moveY += 1;
        if (inputs.left()) moveX -= 1;
        if (inputs.right()) moveX += 1;

        double length = Math.sqrt(moveX * moveX + moveY * moveY);

        if (length > 0) {
            moveX /= length;
            moveY /= length;

            double newX = getX() + moveX * getSpeed();
            double newY = getY() + moveY * getSpeed();
            if (getLevel().isWalkable(newX, newY)) {
                setPos(newX, newY);
            } else if (getLevel().isWalkable(newX, getY())) {
                setPos(newX, getY());
            } else if (getLevel().isWalkable(getX(), newY)){
                setPos(getX(), newY);
            }
        }
    }

    private void updateAttack(Inputs inputs) {
        if (inputs.attack() && attackCooldown <= 0){
            attack();
            log.info("attacking");
            attackCooldown = COOLDOWN_TIME;
        }

    }

    private void updateUltimate(Inputs inputs) {
        if (inputs.ult() && hitsForUlt >= HFULimit){
            useUltimate();
            ultActive = ULT_DURATION;
        }
    }

    private void updateInteraction(Inputs inputs){
        if (inputs.interact()){
            List<Entity> entitiesAround = getLevel().getEntitiesAround(this, this.getReach());
            for (Entity e : entitiesAround) {
                if (e instanceof Chest){
                    ((Chest) e).onInteract(this);
                    break;
                }
            }
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
}