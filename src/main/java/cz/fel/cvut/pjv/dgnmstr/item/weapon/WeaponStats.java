package cz.fel.cvut.pjv.dgnmstr.item.weapon;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Role;

public enum WeaponStats {
    BASIC_WARRIOR_SWORD(30, 50, 5, 10, Role.WARRIOR.getWeaponType(),"Basic Warrior Sword"),
    LEGENDARY_WARRIOR_SWORD(60, 100, 10, 40, Role.WARRIOR.getWeaponType(),"Legendary Warrior Sword"),
    TEST_WARRIOR_SWORD(1000000, 2000, 10, 40, Role.WARRIOR.getWeaponType(),"TEST Warrior Sword");

    private final int damage;
    private final int range;
    private final int dexterity;
    private final int price;
    private final String name;
    private final WeaponType type;

    WeaponStats(int damage, int range, int dexterity, int price, WeaponType type, String name) {
        this.damage = damage;
        this.range = range;
        this.dexterity = dexterity;
        this.price = price;
        this.name = name;
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }


    public int getDexterity() {
        return dexterity;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public WeaponType getType() {
        return type;
    }
}
