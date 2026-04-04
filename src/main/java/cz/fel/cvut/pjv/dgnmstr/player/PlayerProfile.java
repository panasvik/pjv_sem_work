package cz.fel.cvut.pjv.dgnmstr.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fel.cvut.pjv.dgnmstr.entity.Role;
import cz.fel.cvut.pjv.dgnmstr.item.ArmorPiece;
import cz.fel.cvut.pjv.dgnmstr.item.ArmorPieceType;
import cz.fel.cvut.pjv.dgnmstr.item.Weapon;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class PlayerProfile {
    private Map<ArmorPieceType, ArmorPiece> armor;
    private Weapon weapon;
    private Inventory inventory;
    private Role role;
    private int gold;

    public PlayerProfile() {
        this.armor = new EnumMap<>(ArmorPieceType.class);
        this.inventory = new Inventory();
        this.gold = 0;
    }

    public PlayerProfile(Role role) {
        this();
        this.role = role;
    }

    public boolean equipArmor(ArmorPiece newArmor) {
        ArmorPieceType type = newArmor.getType();

        if (role != newArmor.getRole()) {
            System.out.println("Armor doesnt match your role " + role);
            return false;
        }

        ArmorPiece oldArmor = armor.get(type);

        if (oldArmor != null) {
            if (!inventory.addItem(oldArmor)) {
                return false;
            }
        }

        armor.put(type, newArmor);
        inventory.remove(newArmor);
        return true;
    }

    public boolean equipWeapon(Weapon newWeapon) {
        if (role.getWeaponType() != newWeapon.getType()) {
            System.out.println("Wrong weapon for your role " + this.role);
            return false;
        }

        if (this.weapon != null) {
            if (!inventory.addItem(this.weapon)) {
                return false;
            }
        }

        this.weapon = newWeapon;
        inventory.remove(newWeapon);
        return true;
    }
    public static PlayerProfile loadFromJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), PlayerProfile.class);
    }

    public void saveToJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), this);
    }

    @JsonIgnore
    public PlayerStats getPlayerStats() {
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
        return new PlayerStats(damage, defence, hp, range, dexterity, speed, gold);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Map<ArmorPieceType, ArmorPiece> getArmor() { return armor; }
    public void setArmor(Map<ArmorPieceType, ArmorPiece> armor) { this.armor = armor; }
    public Weapon getWeapon() { return weapon; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}