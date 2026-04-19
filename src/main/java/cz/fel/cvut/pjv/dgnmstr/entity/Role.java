package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.item.WeaponType;

public enum Role {
    WARRIOR(WeaponType.MELEE);

    private final WeaponType type;

    Role(WeaponType type) {
        this.type = type;
    }

    public WeaponType getWeaponType() {
        return type;
    }


}