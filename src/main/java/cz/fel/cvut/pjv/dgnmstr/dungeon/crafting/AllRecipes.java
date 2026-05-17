package cz.fel.cvut.pjv.dgnmstr.dungeon.crafting;

import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.item.materials.*;
import cz.fel.cvut.pjv.dgnmstr.item.potions.BigHealPotion;
import cz.fel.cvut.pjv.dgnmstr.item.potions.SmallHealPotion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum AllRecipes {
    SMALL(makeSmall()),
    BIG(makeBig());

    private final Recipe recipe;

    AllRecipes(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    private static Recipe makeSmall() {
        Map<Class<? extends Item>, Integer> ing = Map.of(
                Herb.class, 3,
                Essence.class, 2
        );
        return new Recipe(ing, SmallHealPotion.class, SmallHealPotion::new, "Small Heal Potion");
    }

    private static Recipe makeBig() {
        Map<Class<? extends Item>, Integer> ing = Map.of(
                SmallHealPotion.class, 3,
                Crystal.class, 1
        );
        return new Recipe(ing, BigHealPotion.class, BigHealPotion::new, "Big Heal Potion");
    }

    public static List<Recipe> allRecipes() {
        List<Recipe> list = new ArrayList<>();
        for (AllRecipes r : values()) {
            list.add(r.getRecipe());
        }
        return list;
    }
}


