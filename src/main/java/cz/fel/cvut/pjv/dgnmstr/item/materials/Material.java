package cz.fel.cvut.pjv.dgnmstr.item.materials;

import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

public class Material extends AddableItem {
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
