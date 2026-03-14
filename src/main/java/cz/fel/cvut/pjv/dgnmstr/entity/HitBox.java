package cz.fel.cvut.pjv.dgnmstr.entity;

public class HitBox {
    private double posX, posY;
    private final double radius;

    public HitBox(double posX, double posY, double radius) {
        this.posX = posX;
        this.posY = posY;
        this.radius = radius;
    }

    public boolean isHitMelee(PlayerCursorPos dataPlCsr) {
        double deltaX = dataPlCsr.cursorX() - dataPlCsr.playerX();
        double deltaY = dataPlCsr.cursorY() - dataPlCsr.playerY();
        double k = deltaY / deltaX;
        double estimatedY = k * (this.posX - dataPlCsr.playerX()) + dataPlCsr.playerY();
        double distance = this.posY - estimatedY;
        double cos = 1 / Math.sqrt(Math.pow(k, 2) + 1);
        return Math.abs(distance) * cos <= this.radius;

    }

    public boolean isHitRanged(Projectile proj) {
        double distance = Math.sqrt(Math.pow(this.posX - getPosX(), 2) + Math.pow(this.posY - getPosY(), 2));
        return distance <= proj.getRadius();
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
