package cz.fel.cvut.pjv.dgnmstr.item;

public class Weapon extends AddableItem {

    private final int damage, dexterity, range;
    private final WeaponType type;

    public Weapon(String name, int price, int damage, int dexterity, int range, WeaponType type) {
        super(name, price);
        this.damage = damage;
        this.dexterity = dexterity;
        this.range = range;
        this.type = type;
    }

    public WeaponType getType() {
        return type;
    }

    public int getRange() {
        return range;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getDamage() {
        return damage;
    }
}