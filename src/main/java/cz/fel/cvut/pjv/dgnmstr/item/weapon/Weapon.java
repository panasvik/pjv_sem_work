package cz.fel.cvut.pjv.dgnmstr.item.weapon;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Role;
import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

public abstract class Weapon extends AddableItem {

    private int damage, dexterity, range;
    private final WeaponType type;

    public Weapon(String name, int price, int damage, int dexterity, int range, WeaponType type) {
        super(name, price);
        this.damage = damage;
        this.dexterity = dexterity;
        this.range = range;
        this.type = type;
    }

    protected Weapon() {
        super();
        this.damage = 0;
        this.dexterity = 0;
        this.range = 0;
        this.type = Role.WARRIOR.getWeaponType();
    }

    @Override
    public boolean acceptUsing(PlayerProfile profile) {
        return profile.equipWeapon(this);
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

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public void setRange(int range) {
        this.range = range;
    }
}