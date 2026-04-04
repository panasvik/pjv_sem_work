package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;

public class MyMath {
    public static double sigmoid(double x) {
        return 1 / ( 1 + Math.exp(-x));
    }

    public static double calculateDistanceSquared(Entity a, Entity b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }
}
