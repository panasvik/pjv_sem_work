package cz.fel.cvut.pjv.dgnmstr.entity.hero;

import cz.fel.cvut.pjv.dgnmstr.item.weapon.WeaponType;
/**
 * Selectable hero roles offered at character creation.
 */
public enum Role {
    WARRIOR(WeaponType.MELEE, 20);

    private final WeaponType type;
    private final double halfHitBox;

    Role(WeaponType type, double halfHitBox) {
        this.type = type;
        this.halfHitBox = halfHitBox;
    }

    public WeaponType getWeaponType() {
        return type;
    }


    public double getHalfHitBox() {
        return halfHitBox;
    }
}