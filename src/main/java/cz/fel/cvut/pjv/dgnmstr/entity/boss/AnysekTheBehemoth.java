package cz.fel.cvut.pjv.dgnmstr.entity.boss;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.view.ImgId;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.List;

public class AnysekTheBehemoth extends Boss{
    private boolean rage = false;
    private static final double COOLDOWN_TIME = 0.8;
    private int damageMult = 2;

    private double rageTimer = 0.0;
    private static final double RAGE_DURATION = 10.0;

    /**
     * Creates a new AnysekTheBehemoth.
     * @param id unique identifier
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param level the level this belongs to
     */
    public AnysekTheBehemoth(int id, double positionX, double positionY, Level level) {
        super(id, BossStats.ANYSEK_THE_BEHEMOTH.getHp(level.getDifficulty().getStatsMult()),
              BossStats.ANYSEK_THE_BEHEMOTH.getSpeed(),
              positionX,
              positionY,
              BossStats.ANYSEK_THE_BEHEMOTH.getHalfHitBox(),
              BossStats.ANYSEK_THE_BEHEMOTH.getDamage(level.getDifficulty().getStatsMult()),
              BossStats.ANYSEK_THE_BEHEMOTH.getReach(),
              BossStats.ANYSEK_THE_BEHEMOTH.getGold(),
              Faction.MONSTER,
              level,
              BossStats.ANYSEK_THE_BEHEMOTH.getThreshold());

    }

    @Override
    protected void changePhase() {
        damageMult++;
    }

    @Override
    public void attack() {
        int damage = rage ? getDamage() * damageMult : getDamage();
        List<Entity> entitiesAround = getLevel().getEntitiesAround(this, getReach());
        for (Entity e : entitiesAround) {
            if (this.isEnemy(e)) {
                e.takeDamage(damage, this);
            }
        }
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        super.onDamageTaken(amount, source);

        if (!rage) {
            this.rage = true;
            rageTimer = RAGE_DURATION;
        }
    }

    @Override
    public ImgId getImgId() {
        return ImgId.ANYSEK;
    }

    public void coolRage() {
        this.rage = false;
    }

    /**
     * viz. Boss.java
     */
    @Override
    public void update(double deltaTime) {
        if(!isAlive()){return;}

        Hero target = this.findNearestPlayer();
        if (target == null){ return;}

        this.setLookTarget(target.getX(), target.getY());

        checkMoveToward(target);
        double range = getReach();
        double distS = MyMath.calculateDistanceSquared(this, target);

        if (distS <= range * range){
           if (attackCooldown <= 0){
               attack();
               registerAttack();
               attackCooldown = COOLDOWN_TIME;
           }
        }
        if (rage){
            rageTimer -= deltaTime;
            if (rageTimer <= 0){
                coolRage();
            }
        }

        if (attackCooldown > 0){
            attackCooldown -= deltaTime;
        }
    }
}
