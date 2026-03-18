package cz.fel.cvut.pjv.dgnmstr.item;

import javafx.stage.WindowEvent;

public class ArmorPiece extends Item {
    private final int hp, defence, dexterity, speed;
    private String name;
    private WeaponType type;

    public ArmorPiece(int hp, int defence, int dexterity, int speed, WeaponType type) {
        this.hp = hp;
        this.defence = defence;
        this.dexterity = dexterity;
        this.speed = speed;
        this.type = type;
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

    public String getName() {
        return name;
    }
}
