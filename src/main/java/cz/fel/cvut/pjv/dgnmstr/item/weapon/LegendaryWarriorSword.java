package cz.fel.cvut.pjv.dgnmstr.item.weapon;

public class LegendaryWarriorSword extends Weapon{
    public LegendaryWarriorSword() {
        super(WeaponStats.LEGENDARY_WARRIOR_SWORD.getName(),
                WeaponStats.LEGENDARY_WARRIOR_SWORD.getPrice(),
                WeaponStats.LEGENDARY_WARRIOR_SWORD.getDamage(),
                WeaponStats.LEGENDARY_WARRIOR_SWORD.getDexterity(),
                WeaponStats.LEGENDARY_WARRIOR_SWORD.getRange(), WeaponStats.LEGENDARY_WARRIOR_SWORD.getType());
    }
}
