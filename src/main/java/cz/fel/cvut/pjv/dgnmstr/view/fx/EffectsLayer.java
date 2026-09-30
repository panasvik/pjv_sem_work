package cz.fel.cvut.pjv.dgnmstr.view.fx;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Short-lived visual effects: particle bursts, trails and floating combat text.
 * Everything lives in world coordinates and is purely cosmetic.
 */
public class EffectsLayer {
    private static final int MAX_PARTICLES = 900;
    private static final Font TEXT_FONT = Font.font("EB Garamond", FontWeight.BOLD, 20);

    private final List<Particle> particles = new ArrayList<>();
    private final List<FloatingText> texts = new ArrayList<>();
    private final Random random = new Random();

    private record FloatingText(double x, double y, String text, Color color, double size, double[] life) {
    }

    /**
     * Sprays particles out of a point.
     * @param x world x
     * @param y world y
     * @param color particle color
     * @param count how many particles
     * @param speed initial speed in px/s
     * @param life lifetime in seconds
     * @param gravity downward acceleration
     * @param glow whether they are drawn additively
     */
    public void burst(double x, double y, Color color, int count, double speed, double life, double gravity, boolean glow) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double v = speed * (0.35 + random.nextDouble() * 0.65);
            double size = 2 + random.nextDouble() * 3;
            add(new Particle(x, y, Math.cos(angle) * v, Math.sin(angle) * v - speed * 0.3,
                    size, life * (0.6 + random.nextDouble() * 0.6), color, gravity, 3, glow));
        }
    }

    /**
     * Emits a few slow particles, used for trails and ambient effects.
     */
    public void drift(double x, double y, Color color, double spread, double riseSpeed, double life, boolean glow) {
        double px = x + (random.nextDouble() - 0.5) * spread;
        double py = y + (random.nextDouble() - 0.5) * spread;
        add(new Particle(px, py, (random.nextDouble() - 0.5) * 12, -riseSpeed * (0.5 + random.nextDouble()),
                1.5 + random.nextDouble() * 2.5, life * (0.6 + random.nextDouble() * 0.8), color, 0, 1, glow));
    }

    /**
     * Particles spinning around a centre, used by the ultimate.
     */
    public void swirl(double cx, double cy, double radius, Color color) {
        double angle = random.nextDouble() * Math.PI * 2;
        double r = radius * (0.3 + random.nextDouble() * 0.7);
        double x = cx + Math.cos(angle) * r;
        double y = cy + Math.sin(angle) * r;
        double speed = 260;
        add(new Particle(x, y, -Math.sin(angle) * speed, Math.cos(angle) * speed, 2 + random.nextDouble() * 2,
                0.35, color, 0, 2, true));
    }

    /**
     * Floating text rising from a point (damage numbers, gold pickups).
     */
    public void text(double x, double y, String text, Color color, double size) {
        double jitter = (random.nextDouble() - 0.5) * 18;
        texts.add(new FloatingText(x + jitter, y, text, color, size, new double[]{0}));
    }

    private void add(Particle particle) {
        if (particles.size() >= MAX_PARTICLES) {
            particles.removeFirst();
        }
        particles.add(particle);
    }

    public void update(double dt) {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update(dt);
            if (p.life <= 0) {
                it.remove();
            }
        }
        texts.removeIf(t -> (t.life()[0] += dt) > 1.1);
    }

    /**
     * Draws particles; glowing ones are blended additively.
     */
    public void drawParticles(GraphicsContext gc, double camX, double camY) {
        for (Particle p : particles) {
            if (p.glow) {
                continue;
            }
            drawParticle(gc, p, camX, camY);
        }
        gc.setGlobalBlendMode(BlendMode.ADD);
        for (Particle p : particles) {
            if (p.glow) {
                drawParticle(gc, p, camX, camY);
            }
        }
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        gc.setGlobalAlpha(1);
    }

    private void drawParticle(GraphicsContext gc, Particle p, double camX, double camY) {
        double f = p.fraction();
        double size = p.size * (0.5 + 0.5 * f);
        gc.setGlobalAlpha(Math.min(1, f * 1.6));
        gc.setFill(p.color);
        gc.fillRect(Math.round(p.x - camX - size / 2), Math.round(p.y - camY - size / 2), size, size);
    }

    /**
     * Draws floating texts; they pop in, rise and fade.
     */
    public void drawTexts(GraphicsContext gc, double camX, double camY) {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        for (FloatingText t : texts) {
            double time = t.life()[0];
            double pop = time < 0.12 ? 0.6 + time / 0.12 * 0.7 : Math.max(1.0, 1.3 - (time - 0.12) * 3);
            double alpha = time > 0.7 ? Math.max(0, 1 - (time - 0.7) / 0.4) : 1;
            double x = t.x() - camX;
            double y = t.y() - camY - 26 - time * 38;
            gc.setFont(Font.font(TEXT_FONT.getFamily(), FontWeight.BOLD, t.size() * pop));
            gc.setGlobalAlpha(alpha);
            gc.setLineWidth(3);
            gc.setStroke(Color.rgb(10, 5, 15, 0.9));
            gc.strokeText(t.text(), x, y);
            gc.setFill(t.color());
            gc.fillText(t.text(), x, y);
        }
        gc.setGlobalAlpha(1);
    }

    public void clear() {
        particles.clear();
        texts.clear();
    }
}
