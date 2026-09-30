package cz.fel.cvut.pjv.dgnmstr.view.fx;

import javafx.scene.paint.Color;

/**
 * A single square particle living in world coordinates.
 */
final class Particle {
    double x;
    double y;
    double vx;
    double vy;
    double gravity;
    double drag;
    double size;
    double life;
    final double maxLife;
    final Color color;
    final boolean glow;

    Particle(double x, double y, double vx, double vy, double size, double life, Color color, double gravity, double drag, boolean glow) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.life = life;
        this.maxLife = life;
        this.color = color;
        this.gravity = gravity;
        this.drag = drag;
        this.glow = glow;
    }

    void update(double dt) {
        vy += gravity * dt;
        double damping = Math.exp(-drag * dt);
        vx *= damping;
        vy *= damping;
        x += vx * dt;
        y += vy * dt;
        life -= dt;
    }

    double fraction() {
        return Math.max(0, life / maxLife);
    }
}
