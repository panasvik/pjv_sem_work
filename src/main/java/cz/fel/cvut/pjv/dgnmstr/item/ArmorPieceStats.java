package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public enum ArmorPieceStats {
    BASIC_WARRIOR_CHESTPLATE(70, 20, 0, 10, 20, Role.WARRIOR,"Basic Warrior Chestplate"),
    BASIC_WARRIOR_HELMET(30, 10, 0, 15, 20, Role.WARRIOR, "Basic Warrior Helmet"),
    BASIC_WARRIOR_PANTS(10, 5, 5, 20, 20, Role.WARRIOR, "Basic Warrior Pants"),
    LEGENDARY_WARRIOR_CHESTPLATE(140, 40, 0, 20, 30, Role.WARRIOR, "Legendary Warrior Chestplate"),
    LEGENDARY_WARRIOR_HELMET(60, 20, 0, 30, 30, Role.WARRIOR, "Legendary Warrior Helmet"),
    LEGENDARY_WARRIOR_PANTS(20, 10, 10, 40, 30, Role.WARRIOR, "Legendary Warrior Pants");

    private final int hp;
    private final int armor;
    private final int speed;
    private final int dexterity;
    private final int price;
    private final Role role;
    private final String name;

    ArmorPieceStats(int hp, int armor, int speed, int dexterity, int price, Role role, String name) {
        this.hp = hp;
        this.armor = armor;
        this.speed = speed;
        this.dexterity = dexterity;
        this.price = price;
        this.name = name;
        this.role = role;
    }

    public int getHp() {
        return hp;
    }

    public int getArmor() {
        return armor;
    }

    public int getSpeed() {
        return speed;
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

    public Role getRole() {
        return role;
    }
}


