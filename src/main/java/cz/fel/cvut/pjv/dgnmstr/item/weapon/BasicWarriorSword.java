package cz.fel.cvut.pjv.dgnmstr.item.weapon;

public class BasicWarriorSword extends Weapon{
    public BasicWarriorSword() {
        super(WeaponStats.BASIC_WARRIOR_SWORD.getName(),
                WeaponStats.BASIC_WARRIOR_SWORD.getPrice(),
                WeaponStats.BASIC_WARRIOR_SWORD.getDamage(),
                WeaponStats.BASIC_WARRIOR_SWORD.getDexterity(),
                WeaponStats.BASIC_WARRIOR_SWORD.getRange(), WeaponStats.BASIC_WARRIOR_SWORD.getType());
    }
}
