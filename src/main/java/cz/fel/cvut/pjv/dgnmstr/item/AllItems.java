package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.item.armor.*;
import cz.fel.cvut.pjv.dgnmstr.item.weapon.*;

import java.util.Random;

public enum AllItems {
    BWC(BasicWarriorChestplate.class),
    BWH(BasicWarriorHelmet.class),
    BWP(BasicWarriorPants.class),
    LWC(LegendaryWarriorChestplate.class),
    LWH(LegendaryWarriorHelmet.class),
    LWP(LegendaryWarriorPants.class),
    BWS(BasicWarriorSword.class),
    LWS(LegendaryWarriorSword.class),
    RUBBISH(Rubbish.class);



    private final Class<? extends Item> itemClass;

    AllItems(Class<? extends Item> itemClass) {
        this.itemClass = itemClass;
    }

    public Class<? extends Item> getItemClass() {
        return itemClass;
    }

    private static final Random RANDOM = new Random();

    public static Class<? extends Item> getRandomItemClass() {
        AllItems[] items = values();
        AllItems randomEnum = items[RANDOM.nextInt(items.length)];
        return randomEnum.getItemClass();
    }

    public static Item getRandomItemInstance() {
        try {
            return getRandomItemClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}