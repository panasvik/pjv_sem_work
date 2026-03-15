package cz.fel.cvut.pjv.dgnmstr.entity;


import cz.fel.cvut.pjv.dgnmstr.global.Sigmoid;

import java.util.List;

public abstract class Hero extends Entity implements CanAttack, CanDefend {
    private int damage;
    private int dexterity;
    private int defence;
    private int reach;


    public Hero(int maxHP, int hp, int speed, int positionX, int positionY, List<HitBox> hitBox) {
        super(maxHP, hp, speed, positionX, positionY, hitBox);
    }


    @Override
    protected boolean handleParry(Entity source) {
        if (!(source instanceof CanAttack)) {
            return true;
        }
        if (dextCheck()) {
            source.takeDamage(this.damage, this);
            return true;
        }
        return speedCheck();
    }

    @Override
    public boolean defenceCheck(int damage) {
        return this.defence >= damage * 2;
    }

    @Override
    protected int applyDefence(int damage) {
        if (defenceCheck(damage)) return damage / 2;
        return damage;
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {
        return;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    private boolean dextCheck() {
        double chance = Sigmoid.sigmoid(this.dexterity * this.dexterity);
        double random = Sigmoid.sigmoid(Math.random());
        return random < chance;
    }

    private boolean speedCheck() {
        return getSpeed() > damage;
    }

}
