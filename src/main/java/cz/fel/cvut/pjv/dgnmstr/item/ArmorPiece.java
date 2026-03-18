package cz.fel.cvut.pjv.dgnmstr.item;

import javafx.stage.WindowEvent;

public class ArmorPiece extends Item {
    private final int hp, defence, dexterity, speed;
    private String name;
    private ArmorPieceType type;

    public ArmorPiece(int hp, int defence, int dexterity, int speed, ArmorPieceType type) {
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

    public ArmorPieceType getType() {
        return type;
    }
}
