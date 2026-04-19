package cz.fel.cvut.pjv.dgnmstr.entity;



import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;
import cz.fel.cvut.pjv.dgnmstr.entity.Role;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

public abstract class Hero extends Entity implements CanAttack, CanDefend, CanUltimate {
    private int damage;
    private int dexterity;
    private int defence;
    private int reach;
    private final Role role;
    private int gold;
    private final PlayerProfile profile;

    public Hero(PlayerProfile profile, double positionX, double positionY, Role role, Faction faction, Level level) {
        super(profile.getPlayerStats().hp(), profile.getPlayerStats().speed(), positionX, positionY, faction, level);
        PlayerStats stats = profile.getPlayerStats();
        this.damage = stats.damage();
        this.dexterity = stats.dexterity();
        this.defence = stats.defence();
        this.reach = stats.range();
        this.role = role;
        this.gold = 0;
        this.profile = profile;
    }


    /**
     * decides whether this should take the damage dealt, parry or dodge
     * checks dexterity, speed and reach to decide
     * @param source who is dealing damage
     * @return will be the attack parried or not
     */
    @Override
    protected boolean handleParry(Entity source) {
        if (!(source instanceof CanAttack)) {
            return false;
        }
        if (dextCheck() && getReach() * getReach() >= MyMath.calculateDistanceSquared(this, source)) {
            source.takeDamage(this.damage, this);
            return true;
        }
        return speedCheck();
    }


    @Override
    public boolean defenceCheck(int damage) {
        return this.defence >= damage * 2;
    }

    @Override
    protected int applyDefence(int damage) {
        if (defenceCheck(damage)) return damage / 2;
        return damage;
    }


    @Override
    public int getDamage() {
        return damage;
    }

    /**
     * checks whether this can parry
     * @return true can / false cannot
     */

    private boolean dextCheck() {
        double chance = MyMath.sigmoid(this.dexterity * this.dexterity);
        double random = Math.random();
        return random < chance;
    }

    private boolean speedCheck() {
        return getSpeed() > damage;
    }

    public int getReach() {
        return reach;
    }

    public Role getRole() {
        return role;
    }

    protected void setDamage(int damage) {
        this.damage = damage;
    }

    protected void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    protected void setDefence(int defence) {
        this.defence = defence;
    }

    protected void setReach(int reach) {
        this.reach = reach;
    }

    protected int getDexterity() {
        return dexterity;
    }

    protected int getDefence() {
        return defence;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public int getGold() {
        return gold;
    }


    /**
     * loads new stats of PlayerProfile wrapped int PlayerStats
     * @param stats new stats
     */
    public void syncStats(PlayerStats stats) {
        this.setMaxHP(stats.hp());
        this.setDamage(stats.damage());
        this.setDexterity(stats.dexterity());
        this.setDefence(stats.defence());
        this.setReach(stats.range());
        this.setSpeed(stats.speed());
        this.gold = stats.gold();
        setHp(Math.min(getHp(), getMaxHP()));
    }

    public PlayerProfile getProfile() {
        return profile;
    }

}
