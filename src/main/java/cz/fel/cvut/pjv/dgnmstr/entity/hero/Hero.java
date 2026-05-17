package cz.fel.cvut.pjv.dgnmstr.entity.hero;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanAttack;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanDefend;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.CanUltimate;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;

/**
 * Player-controlled entity. Wraps a {@link cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile}
 * which carries persistent data (stats, gold, inventory) while the
 * Hero itself holds the live in-world state.
 */
public abstract class Hero extends Entity implements CanAttack, CanDefend, CanUltimate {
    private int damage;
    private int dexterity;
    private int defence;
    private int reach;
    private final Role role;
    private int gold;
    private PlayerProfile profile;

    protected Inputs currentInputs;
    /**
     * Creates a new Hero.
     * @param id unique identifier
     * @param profile the player profile
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param halfHitBox half the collision box size
     * @param role the chosen role
     * @param faction side this belongs to
     * @param level the level this belongs to
     */
    public Hero(int id, PlayerProfile profile, double positionX, double positionY, double halfHitBox, Role role, Faction faction, Level level) {
        super(id, profile.getPlayerStats().hp(), profile.getPlayerStats().speed(), positionX, positionY, halfHitBox, faction, level);
        PlayerStats stats = profile.getPlayerStats();
        this.damage = stats.damage();
        this.dexterity = stats.dexterity();
        this.defence = stats.defence();
        this.reach = stats.range();
        this.role = role;
        this.gold = profile.getGold();
        this.profile = profile;
    }

    public void setInputs(Inputs inputs) {
        this.currentInputs = inputs;
    }

    /**
     * Heros specific parry. A dexterity check can fully parry and
     * counter-attack within reach; otherwise a speed check decides
     * whether the blow is dodged.
     * @param source the attacking entity
     * @return true if the attack was parried (no damage taken)
     */
    @Override
    protected boolean handleParry(Entity source) {
        if (!(source instanceof CanAttack attackSource)) {
            return false;
        }
        if (dextCheck() && getReach() * getReach() >= MyMath.calculateDistanceSquared(this, source)) {
            source.takeDamage(this.damage, this);
            return true;
        }
        return speedCheck(attackSource.getDamage());
    }

    /**
     * * Whether the hero's defence is high enough to halve this hit
     * (defence must be at least twice the incoming damage).
     * @param damage incoming raw damage
     * @return true if the damage will be reduced
     */
    @Override
    public boolean defenceCheck(int damage) {
        return this.defence >= damage * 2;
    }

    /**
     * Reduces incoming damage by half when {@link #defenceCheck(int)}
     * passes, otherwise lets it through unchanged.
     * @param damage raw incoming damage
     * @return damage after defence
     */
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
        double chance = MyMath.sigmoid(dexterity);
        double random = Math.random();
        return random < chance;
    }

    private boolean speedCheck(int damage) {
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
        profile.setGold(gold);
    }

    public int getGold() {
        return gold;
    }

    /**
     * Overwrites the hero's combat stats from a server-provided
     * snapshot. Used to keep the client hero in sync with the
     * authoritative server state.
     * @param stats the stat snapshot to apply
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

    /**
     * Advances movement by one game tick.
     * @param inputs inputs
     */
    protected void updateMovement(Inputs inputs) {
        double moveX = 0;
        double moveY = 0;

        if (inputs.up()) moveY -= 1;
        if (inputs.down()) moveY += 1;
        if (inputs.left()) moveX -= 1;
        if (inputs.right()) moveX += 1;

        double length = Math.sqrt(moveX * moveX + moveY * moveY);

        if (length > 0) {
            moveX = moveX * getSpeed() / length;
            moveY = moveY * getSpeed() / length;

            tryStepTo(moveX, moveY);
        }
    }

    public void setGold(int gold) {
        this.profile.setGold(gold);
        this.gold = gold;
    }

    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
        syncStats(profile.getPlayerStats());
    }
}