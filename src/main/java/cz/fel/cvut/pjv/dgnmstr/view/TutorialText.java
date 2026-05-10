package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Warrior;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

public enum TutorialText {
    MOVEMENT("Use WASD to move your Hero across the map. You can only walk on empty floor tiles and must avoid obstacles like fences and chests.", "MOVEMENT"),
    ATTACKING("Attack with the Left Mouse Button (LMB). Your attacks hit in a 180-degree arc with a cooldown of " + Warrior.COOLDOWN_TIME + " seconds. Your ultimate ability charges after " + Warrior.HFULimit + " consecutive hits on enemies without taking damage. Once charged, press E to activate. It lasts for " + Warrior.ULT_DURATION + " seconds and deals " + Warrior.DAMAGE_MUL + " times normal damage.", "ATTACKING"),
    INVENTORY("Open your inventory by pressing the 'I' key.", "INVENTORY"),
    CHESTS("You can open a nearby chest for " + Chest.PRICE_TO_OPEN_CHEST + " gold by pressing F. Once a chest is unlocked, it does not cost gold to open it again.", "CHESTS"),
    ITEMS("Transfer items between an open chest and your inventory by double-clicking on them. Both your inventory and chests have a maximum capacity of " + Inventory.MAX_SIZE + " items.", "ITEM INTERACTION"),
    ARMOR_WEAPON("When your inventory is open (and no chest is open), you can equip weapons and armor by double-clicking on the item.", "EQUIPMENT"),
    ARENA("When you enter an Arena, the grates will close, trapping you inside! To progress, you must defeat all the enemies. Once all regular enemies are cleared, a powerful enemy will spawn. Defeat it to reopen the grates.", "ARENA COMPLETION"),
    LEVEL("After all arenas are cleared, the final Boss will spawn in the last Arena. Defeat the Boss to complete the level by stepping on the exit tiles at the far end.", "LEVEL COMPLETION");

    private final String message;
    private final String name;

    TutorialText(String message, String name) {
        this.message = message;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }
}
