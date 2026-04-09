package cz.fel.cvut.pjv.dgnmstr.item;


import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public class ArmorPiece extends AddableItem {
    private final int hp, defence, dexterity, speed;
    private final ArmorPieceType type;
    private final Role role;

    public ArmorPiece(String name, int price, Role role, ArmorPieceType type, int speed, int dexterity, int defence, int hp) {
        super(name, price);
        this.role = role;
        this.type = type;
        this.speed = speed;
        this.dexterity = dexterity;
        this.defence = defence;
        this.hp = hp;
    }

    public int getHp() {
        return hp;
    }

    public int getDefence() {
        return defence;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getSpeed() {
        return speed;
    }

    public Role getRole() {
        return role;
    }

    public ArmorPieceType getType() {
        return type;
    }
}