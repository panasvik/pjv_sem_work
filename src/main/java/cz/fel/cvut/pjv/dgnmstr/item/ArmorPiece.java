package cz.fel.cvut.pjv.dgnmstr.item;

public class ArmorPiece extends Item {
    private int hp, armor, dexterity, speed;
    private String name;

    public ArmorPiece(int hp, int armor, int dexterity, int speed) {
        this.hp = hp;
        this.armor = armor;
        this.dexterity = dexterity;
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public int getArmor() {
        return armor;
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
