package cz.fel.cvut.pjv.dgnmstr.dungeon.crafting;

import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.util.Map;
import java.util.function.Supplier;

public class Recipe {
    private final Map<Class<? extends Item>, Integer> ingredients;
    private final Class<? extends Item> result;
    private final Supplier<Item> resultFactory;
    private final String name;

    public Recipe(Map<Class<? extends Item>, Integer> ingredients, Class<? extends Item> result, Supplier<Item> resultFactory, String name) {
        this.ingredients = ingredients;
        this.result = result;
        this.resultFactory = resultFactory;
        this.name = name;
    }

    public Map<Class<? extends Item>, Integer> getIngredients() {
        return ingredients;
    }

    public String getName() {
        return name;
    }

    public Item createResult() {
        return resultFactory.get();
    }

    public Class<? extends Item> getResult() {
        return result;
    }
    public boolean isCraftable(PlayerProfile profile) {
        Inventory inventory = profile.getInventory();
        for (Map.Entry<Class<? extends Item>, Integer> e : ingredients.entrySet()) {
            int count = inventory.getCount(e.getKey());
            if (count < e.getValue()) {
                return false;
            }
        }
        return true;
    }
}
