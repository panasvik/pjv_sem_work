package cz.fel.cvut.pjv.dgnmstr.entity;

public class HitBox {
    private int posX, poxY, radius;

    public HitBox(int posX, int poxY, int radius) {
        this.posX = posX;
        this.poxY = poxY;
        this.radius = radius;
    }

    public int getPosX() {
        return posX;
    }

    public int getPoxY() {
        return poxY;
    }

    public int getRadius() {
        return radius;
    }
}
