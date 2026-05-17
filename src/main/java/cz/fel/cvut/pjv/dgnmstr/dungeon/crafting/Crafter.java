package cz.fel.cvut.pjv.dgnmstr.dungeon.crafting;

import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.item.materials.*;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crafter {
    public boolean craft(Recipe recipe, PlayerProfile profile) {
        if (!recipe.isCraftable(profile)) {
            return false;
        }

        Inventory inventory = profile.getInventory();
        List<Item> toRemove = new ArrayList<>();
        for (Map.Entry<Class<? extends Item>, Integer> e : recipe.getIngredients().entrySet()) {
            Class<? extends Item> type = e.getKey();
            int needed = e.getValue();
            int collected = 0;
            for (Item it : inventory.getInvent()) {
                if (collected >= needed) {
                    break;
                }
                if (it.getClass().equals(type)) {
                    toRemove.add(it);
                    collected++;
                }
            }
            if (collected < needed) {
                return false;
            }
        }
        for (Item i : toRemove) {
            inventory.remove(i);
        }
        inventory.addItem(recipe.createResult());
        return true;
    }
}

