package cz.fel.cvut.pjv.dgnmstr.item.materials;

import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
/**
 * Base class for crafting materials. Materials cannot be used
 * directly from the inventory.
 */
public class Material extends AddableItem {
    /**
     * Creates a new Material.
     * @param name display name
     * @param price price value
     */
    public Material(String name, int price) {
        super(name, price);
    }

    protected Material() {
        super();
    }

    @Override
    public boolean acceptUsing(PlayerProfile profile) {
        return false;
    }
}
