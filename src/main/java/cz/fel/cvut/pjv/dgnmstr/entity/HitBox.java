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
        double deltaXHB = dataPlCsr.cursorX() - this.posX;
        double deltaYHB = dataPlCsr.cursorY() - this.posY;
        double tgB = deltaYHB / deltaXHB;

        double distanceHB = Math.sqrt(deltaXHB * deltaXHB + deltaYHB * deltaYHB);
        if (distanceHB > dataPlCsr.range()) { // range check
            return false;
        }
        // within range
        double deltaXCsr = dataPlCsr.cursorX() - dataPlCsr.playerX();
        double deltaYCsr = dataPlCsr.cursorY() - dataPlCsr.playerY();
        double tgA = deltaYCsr / deltaXCsr;
        double tgDelta = (tgA - tgB) / (1 + tgA * tgB);
        return Math.abs(tgDelta) <= 1;
        // is hit when tg is within +- 1, or when angle is between +- 45 deg.

    }

    public boolean isHitProjectile(Projectile proj) {
        double deltaX = this.posX - getPosX();
        double deltaY = this.posY - getPosY();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
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
