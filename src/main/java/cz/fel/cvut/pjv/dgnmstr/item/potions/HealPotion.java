package cz.fel.cvut.pjv.dgnmstr.item.potions;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

public abstract class HealPotion extends AddableItem {
    public HealPotion(String name, int price) {
        super(name, price);
    }
    protected HealPotion() {
        super();
    }
    public abstract double getHealPercentage();

    @Override
    public boolean acceptUsing(PlayerProfile profile) {
        return false;
    }
}
