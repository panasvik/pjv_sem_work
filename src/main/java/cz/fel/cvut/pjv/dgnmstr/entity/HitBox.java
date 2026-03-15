package cz.fel.cvut.pjv.dgnmstr.entity;

public class HitBox {
    private double posX, posY;
    private final double radius;

    public HitBox(double posX, double posY, double radius) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
    }


    public void setPos(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getRadius() {
        return radius;
    }
}
