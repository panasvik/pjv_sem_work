package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.item.ArmorPiece;
import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.item.Weapon;

import java.util.List;

public class Player {
    private List<ArmorPiece> armor;
    private Weapon weapon;
    private List<Item> inventory;
    private final static int MAX_INVENTORY_SIZE = 16;
    private int curInvSize;
    private Role role;

    // Constructor undone


    public List<ArmorPiece> getArmor() {
        return armor;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean addItem(Item item) {
        if (curInvSize < MAX_INVENTORY_SIZE) {
            inventory.add(item);
            return true;
        }
        return false;
    }

    public boolean changeWeapon(Weapon weapon) {
        if (curInvSize >= MAX_INVENTORY_SIZE ||
                role.getWeaponType() != weapon.getType()) {
            return false;
        }
        inventory.add(this.weapon);
        this.weapon = weapon;
        return true;

    }
}
