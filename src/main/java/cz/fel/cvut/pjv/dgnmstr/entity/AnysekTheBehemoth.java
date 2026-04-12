package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

import java.util.List;

public class AnysekTheBehemoth extends Boss{
    private boolean rage = false;
    private int coolDown = 8;
    private int damageMult = 2;
    public AnysekTheBehemoth(double positionX, double positionY, Level level) {
        super(BossStats.ANYSEK_THE_BEHEMOTH.getHp(level.getDifficulty().getStatsMult()),
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
        coolDown--;
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
        this.rage = true;
    }

    public void coolRage() {
        this.rage = false;
    }
}
