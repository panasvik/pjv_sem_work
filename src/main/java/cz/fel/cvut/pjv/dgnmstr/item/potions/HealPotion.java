package cz.fel.cvut.pjv.dgnmstr.item.potions;

import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
/**
 * Base class for potions that restore hit points. Healing is
 * applied authoritatively on the server, so acceptUsing
 * intentionally does nothing here.
 */
public abstract class HealPotion extends AddableItem {
    /**
     * Creates a new HealPotion.
     * @param name display name
     * @param price price value
     */
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
