package cz.fel.cvut.pjv.dgnmstr.entity;


import java.util.List;

public class Hero extends Entity{
    private int dexterity;
    private int defence;
    private int reach;

    public Hero(int maxHP, int hp, int speed, int positionX, int positionY, List<HitBox> hitBox) {
        super(maxHP, hp, speed, positionX, positionY, hitBox);
    }

    @Override
    public void takeDamage(int damage) {
        //TODO
    }
}
