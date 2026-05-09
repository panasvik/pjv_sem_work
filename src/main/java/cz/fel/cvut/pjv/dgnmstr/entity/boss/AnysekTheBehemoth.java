package cz.fel.cvut.pjv.dgnmstr.entity.boss;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

import java.util.List;

public class AnysekTheBehemoth extends Boss{
    private boolean rage = false;
    private static final double COOLDOWN_TIME = 3.0;
    private int damageMult = 2;

    private double rageTimer = 0.0;
    private static final double RAGE_DURATION = 5.0; // doladit
    public AnysekTheBehemoth(int id, double positionX, double positionY, Level level) {
        super(id, BossStats.ANYSEK_THE_BEHEMOTH.getHp(level.getDifficulty().getStatsMult()),
              BossStats.ANYSEK_THE_BEHEMOTH.getSpeed(),
              positionX,
              positionY,
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

    @Override
    public void update(double deltaTime) {
        if(!isAlive()){return;}

        Hero target = this.findNearestPlayer();
        if (target == null){ return;}

        this.setLookTarget(target.getCenterX(), target.getCenterY());

        checkMoveToward(target);
        double range = getReach();
        double distS = MyMath.calculateDistanceSquared(this, target);
        double dist = Math.sqrt(distS);

        if (dist <= range){
           if (attackCooldown <= 0){
               attack();
               attackCooldown =  COOLDOWN_TIME;
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
