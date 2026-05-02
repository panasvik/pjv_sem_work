package cz.fel.cvut.pjv.dgnmstr.item.weapon;

public class TESTWarriorSword extends Weapon{
    public TESTWarriorSword() {
        super(WeaponStats.BASIC_WARRIOR_SWORD.getName(),
                WeaponStats.BASIC_WARRIOR_SWORD.getPrice(),
                1000000,
                WeaponStats.BASIC_WARRIOR_SWORD.getDexterity(),
                2000, WeaponStats.BASIC_WARRIOR_SWORD.getType());
    }
}
