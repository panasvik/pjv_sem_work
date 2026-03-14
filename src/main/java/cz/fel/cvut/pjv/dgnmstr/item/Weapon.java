package cz.fel.cvut.pjv.dgnmstr.item;

public class Weapon extends Item {

    private int damage, dexterity, range;
    private WeaponType type;

    public Weapon(int damage, int dexterity, int range, WeaponType type) {
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
