package cz.fel.cvut.pjv.dgnmstr.entity;

public class Projectile {
    private int posX;
    private int posY;
    private final int radius;

    public Projectile(int radius, int posY, int posX) {
        this.radius = radius;
        this.posY = posY;
        this.posX = posX;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getRadius() {
        return radius;
    }

    public void setPos(int posX, int posY) {
        this.posY = posY;
        this.posX = posX;
    }

}
