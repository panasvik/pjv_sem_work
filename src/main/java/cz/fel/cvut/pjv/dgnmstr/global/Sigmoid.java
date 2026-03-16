package cz.fel.cvut.pjv.dgnmstr.global;

public class Sigmoid {
    public static double sigmoid(double x) {
        return 1 / ( 1 + Math.exp(-x));
    }
}
