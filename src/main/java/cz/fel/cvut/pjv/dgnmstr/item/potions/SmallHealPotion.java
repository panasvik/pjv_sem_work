package cz.fel.cvut.pjv.dgnmstr.item.potions;

public class SmallHealPotion extends HealPotion{
    public SmallHealPotion() {
        super("Small Heal Potion", 20);
    }

    @Override
    public double getHealPercentage() {
        return 0.25;
    }

}
