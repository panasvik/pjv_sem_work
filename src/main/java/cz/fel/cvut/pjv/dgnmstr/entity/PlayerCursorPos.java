package cz.fel.cvut.pjv.dgnmstr.entity;

public record PlayerCursorPos(
        double playerX, double playerY, double cursorX, double cursorY, int range) {}
