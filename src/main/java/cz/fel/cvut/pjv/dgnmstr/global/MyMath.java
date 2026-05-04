package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Positionable;

public class MyMath {
    public static final double SCALE_FACTOR = 50;
    public static double sigmoid(double x) {
        double midpoint = 100.0;
        double scale = 36.07;

        return 1 / (1 + Math.exp(-(x - midpoint) / scale));
    }

    public static <T extends Positionable> double calculateDistanceSquared(T a, T b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }

    public static double calculateDistanceSquared(double dx, double dy) {
        return dx * dx + dy * dy;
    }
}
