package cz.fel.cvut.pjv.dgnmstr.item.potions;

public class BigHealPotion extends HealPotion{
    public BigHealPotion() {
        super("Big Heal Potion", 75);
    }
    @Override
    public double getHealPercentage() {
        return 1.00;
    }
}
