package cz.fel.cvut.pjv.dgnmstr.entity.hero;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanAttackMelee;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;

import java.util.List;
import java.util.logging.Logger;

public class Warrior extends Hero implements CanAttackMelee {
    public static final int HFULimit = 5;
    private int hitsForUlt = 0;
    private static final Logger log = Logger.getLogger(Warrior.class.getName());

    private double attackCooldown = 0.0;
    public static final double COOLDOWN_TIME = 0.2;

    private double ultActive = 0.0;
    public static final double ULT_DURATION = 8.0;
    public static final int DAMAGE_MUL = 5;

    public static final double B_STORM_RADIUS_MUL = 5.0;
    private double bladeStormDamage = 0.0;

    public Warrior(int id, PlayerProfile profile, double posX, double posY, Level level) {
        super(id, profile, posX, posY, Role.WARRIOR.getHalfHitBox(), Role.WARRIOR, Faction.HERO, level);
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
        return getReach() * getReach() > MyMath.calculateDistanceSquared(this, entity);
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

        return squaredDot / (squaredMagE * squaredMagC) > 0;
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
        List<Entity> entitiesAround = getLevel().getEntitiesAround(this, getReach() * B_STORM_RADIUS_MUL);
        for (Entity e : entitiesAround) {
            if (this.isEnemy(e) && isInAngle(e, getLookX(), getLookY())) {
                e.takeDamage(getDamage() * DAMAGE_MUL, this);
            }
        }
        hitsForUlt = 0;
    }

    @Override
    public void update(double deltaTime) {
        if (!isAlive()) return;

        if (currentInputs != null) {
            setLookTarget(currentInputs.mouseX() + getX(), currentInputs.mouseY() + getY());

            updateMovement(currentInputs);
            updateAttack(currentInputs);
            updateUltimate(currentInputs);
            currentInputs = null;
        }

        updateBladeStorm(deltaTime);
        updateCooldowns(deltaTime);
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


    private void updateBladeStorm(double deltaTime){
        if (ultActive > 0) {
            double damPerSecond = getDamage()/2.0;
            bladeStormDamage += damPerSecond * deltaTime;

            if (bladeStormDamage >= 1.0) {
                int damage = (int) bladeStormDamage;
                double bladeStormRadius = B_STORM_RADIUS_MUL * getReach();
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