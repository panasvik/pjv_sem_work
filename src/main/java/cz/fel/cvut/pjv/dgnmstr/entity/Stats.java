package cz.fel.cvut.pjv.dgnmstr.entity;

public enum Stats {
    ZOMBIE(70, 10, 20, 5, 50),
    GHOST(50, 15, 15, 5, 100),
    EVIL_WIZARD (30, 10, 25, 40, 150); //reach má být neomezen nezapomenout

    private final int hp;
    private final int speed;
    private final int damage;
    private final int reach;
    private final int gold;

    Stats(int hp, int speed, int damage, int reach, int gold) {
        this.hp = hp;
        this.speed = speed;
        this.damage = damage;
        this.reach = reach;
        this.gold = gold;
    }

    public int getHp(double diffMod) {
        return (int) (hp * diffMod);
    }

    public int getSpeed() {
        return speed;
    }

    public int getDamage(double diffMod) {
        return (int) (damage * diffMod);
    }

    public int getReach() {
        return reach;
    }

    public int getGold() {
        return gold;
    }
}

