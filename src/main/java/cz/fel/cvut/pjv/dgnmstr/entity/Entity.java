package cz.fel.cvut.pjv.dgnmstr.entity;


import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;

public abstract class Entity {
    private int maxHP;
    private int hp;
    private int speed;
    private boolean alive;
    private double positionX, positionY;
    private double lookX, lookY;
    private final Faction faction;
    private final Level level;

    public Entity(int hp, int speed, double positionX, double positionY, Faction faction, Level level) {
        this.maxHP = hp;
        this.hp = hp;
        this.speed = speed;
        this.alive = true;
        this.positionX = positionX;
        this.positionY = positionY;
        this.faction = faction;
        this.level = level;

    }

    public int getHp() {
        return hp;
    }

    public final void takeDamage(int damage, Entity source) {
        if (damage <= 0) return;
        if (handleParry(source)) return;
        damage = applyDefence(damage);
        this.hp -= damage;
        onDamageTaken(damage, source);
        if (this.hp <= 0) {
            die();
        }
    }

    protected abstract void onDamageTaken(int amount, Entity source);

    protected boolean handleParry(Entity source) {
        return !(source instanceof CanAttack);
    }

    protected abstract int applyDefence(int damage);

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(hp, this.maxHP));
    }

    public boolean isAlive() {
        return alive;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void die() {
        this.alive = false;
        this.hp = 0;
    }


    public int getMaxHP() {
        return maxHP;
    }

    public double getX() {
        return positionX;
    }

    public double getY() {
        return positionY;
    }

    public void setPos(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public boolean isEnemy(Entity other) {
        if (this.faction == Faction.NEUTRAL) {
            return false;
        }
        return this.faction != other.faction;
    }

    public void setLookTarget(double targetX, double targetY) {
        this.lookX = targetX;
        this.lookY = targetY;
    }

    public double getLookX() {
        return lookX;
    }

    public double getLookY() {
        return lookY;
    }

    public Level getLevel() {
        return level;
    }

    protected void setMaxHP(int maxHP) {
        this.maxHP = Math.max(0, maxHP);
    }

    public void update() {}
}
