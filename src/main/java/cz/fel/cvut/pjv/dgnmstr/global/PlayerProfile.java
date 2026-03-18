package cz.fel.cvut.pjv.dgnmstr.global;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fel.cvut.pjv.dgnmstr.item.ArmorPiece;
import cz.fel.cvut.pjv.dgnmstr.item.ArmorPieceType;
import cz.fel.cvut.pjv.dgnmstr.item.Item;
import cz.fel.cvut.pjv.dgnmstr.item.Weapon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PlayerProfile {
    private Map<ArmorPieceType, ArmorPiece> armor;
    private Weapon weapon;
    private List<Item> inventory;

    @JsonIgnore
    private final static int MAX_INVENTORY_SIZE = 16;
    private Role role;

    public PlayerProfile() {
        this.armor = new EnumMap<>(ArmorPieceType.class);
        this.inventory = new ArrayList<>();
    }

    public static PlayerProfile loadFromJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), PlayerProfile.class);
    }

    public void saveToJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), this);
    }

    public boolean equipArmor(ArmorPiece newArmor) {
        ArmorPieceType type = newArmor.getType();
        if (armor.containsKey(type) && inventory.size() >= MAX_INVENTORY_SIZE) {
            return false;
        }

        ArmorPiece oldArmor = armor.get(type);
        if (oldArmor != null) {
            inventory.add(oldArmor);
        }

        armor.put(type, newArmor);
        inventory.remove(newArmor);
        return true;
    }

    public boolean equipWeapon(Weapon newWeapon) {
        if (role != null && role.getWeaponType() != newWeapon.getType()) {
            return false;
        }
        if (this.weapon != null && inventory.size() >= MAX_INVENTORY_SIZE) {
            return false;
        }

        if (this.weapon != null) {
            inventory.add(this.weapon);
        }
        this.weapon = newWeapon;
        inventory.remove(newWeapon);
        return true;
    }


    @JsonIgnore
    public PlaterStats getPlayerStats() {
        int damage = (weapon != null) ? weapon.getDamage() : 0;
        int range = (weapon != null) ? weapon.getRange() : 0;
        int dexterity = (weapon != null) ? weapon.getDexterity() : 0;
        int defence = 0, speed = 0, hp = 0;

        for (ArmorPiece piece : armor.values()) {
            defence += piece.getDefence();
            dexterity += piece.getDexterity();
            speed += piece.getSpeed();
            hp += piece.getHp();
        }
        return new PlaterStats(damage, defence, hp, range, dexterity, speed);
    }


    public Map<ArmorPieceType, ArmorPiece> getArmor() {
        return armor;
    }

    public void setArmor(Map<ArmorPieceType, ArmorPiece> armor) {
        this.armor = armor;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public List<Item> getInventory() { return inventory; }
    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}