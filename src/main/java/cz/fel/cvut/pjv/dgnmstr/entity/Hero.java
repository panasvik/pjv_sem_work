package cz.fel.cvut.pjv.dgnmstr.entity;


import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;
import cz.fel.cvut.pjv.dgnmstr.global.Sigmoid;

public abstract class Hero extends Entity implements CanAttack, CanDefend, CanUltimate {
    private int damage;
    private int dexterity;
    private int defence;
    private int reach;
    private Role role;


    public Hero(int hp, int speed, double positionX, double positionY, int damage, int dexterity, int defence, int reach, Role role, Faction faction, Level level) {
        super(hp, speed, positionX, positionY, faction, level);
        this.damage = damage;
        this.dexterity = dexterity;
        this.defence = defence;
        this.reach = reach;
        this.role = role;
    }

    @Override
    protected boolean handleParry(Entity source) {
        if (!(source instanceof CanAttack)) {
            return false;
        }
        if (dextCheck()) {
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

    private boolean dextCheck() {
        double chance = Sigmoid.sigmoid(this.dexterity * this.dexterity);
        double random = Math.random();
        return random < chance;
    }

    @Override
    public void die() {
        super.die();
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

    public void syncStats(PlayerStats stats) {
        this.setMaxHP(stats.hp());
        this.setDamage(stats.damage());
        this.setDexterity(stats.dexterity());
        this.setDefence(stats.defence());
        this.setReach(stats.range());
        this.setSpeed(stats.speed());
        setHp(Math.min(getHp(), getMaxHP()));
    }
}
