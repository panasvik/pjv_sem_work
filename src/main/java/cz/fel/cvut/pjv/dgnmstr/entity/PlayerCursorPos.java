package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.item.WeaponType;

public record PlayerCursorPos(
        double playerX, double playerY, double cursorX, double cursorY, int range) {

}
