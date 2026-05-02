package cz.fel.cvut.pjv.dgnmstr.item.weapon;

public class TESTWarriorSword extends Weapon{
    public TESTWarriorSword() {
        super(WeaponStats.TEST_WARRIOR_SWORD.getName(),
                WeaponStats.TEST_WARRIOR_SWORD.getPrice(),
                WeaponStats.TEST_WARRIOR_SWORD.getDamage(),
                WeaponStats.TEST_WARRIOR_SWORD.getDexterity(),
                WeaponStats.TEST_WARRIOR_SWORD.getRange(), WeaponStats.TEST_WARRIOR_SWORD.getType());
    }
}
