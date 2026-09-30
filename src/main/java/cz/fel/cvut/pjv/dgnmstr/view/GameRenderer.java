package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Arena;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.Boss;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Warrior;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.entity.map.DroppedGold;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Fence;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.Projectile;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.global.ImgLoader;
import cz.fel.cvut.pjv.dgnmstr.view.anim.EntityAnimator;
import cz.fel.cvut.pjv.dgnmstr.view.anim.SpriteSet;
import cz.fel.cvut.pjv.dgnmstr.view.anim.Sprites;
import cz.fel.cvut.pjv.dgnmstr.view.dungeon.DungeonPainter;
import cz.fel.cvut.pjv.dgnmstr.view.dungeon.DungeonTheme;
import cz.fel.cvut.pjv.dgnmstr.view.fx.EffectsLayer;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Game renderer.
 * <p>
 * Draws the dungeon, animated entities and effects every frame. The client
 * only receives entity snapshots, so the renderer compares them frame to frame
 * to detect spawns, hits, attacks and deaths and turns them into animation
 * transitions and particle effects.
 */
public class GameRenderer extends AnimationTimer {
    private static final String MAP = "/cz/fel/cvut/pjv/dgnmstr/entity/map/";
    private static final String PROJECTILES = "/cz/fel/cvut/pjv/dgnmstr/entity/projectiles/";
    private static final double MAX_DT = 0.05;
    private static final double CAMERA_STIFFNESS = 10;
    private static final double INTRO_TIME = 1.4;
    private static final double OUTRO_TIME = 3.4;
    private static final double TITLE_TIME = 3.2;
    private static final double RISE_TIME = 0.45;
    private static final double HP_BAR_TIME = 3.5;
    private static final double SPAWN_RING_TIME = 0.8;
    private static final double CHEST_RANGE = Math.sqrt(Chest.MAX_DIST_TO_OPEN);

    private static final Font TITLE_FONT = Font.font("Pirata One", 68);
    private static final Font SUBTITLE_FONT = Font.font("Cinzel", FontWeight.BOLD, 20);
    private static final Font BOSS_FONT = Font.font("Pirata One", 30);
    private static final Font PROMPT_FONT = Font.font("EB Garamond", FontWeight.BOLD, 16);

    private static final Color GOLD = Color.rgb(230, 185, 70);
    private static final Color BLOOD = Color.rgb(150, 20, 30);

    private final Level level;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Hero localHero;
    private final GameManager gameManager;
    private final DungeonPainter painter;
    private final EffectsLayer effects = new EffectsLayer();
    private final Random random = new Random();

    private final Map<Integer, EntityAnimator> animators = new HashMap<>();
    private final Map<Integer, Entity> known = new HashMap<>();
    private final Map<Integer, Double> appeared = new HashMap<>();
    private final Map<Integer, Double> lastHurt = new HashMap<>();
    private final Map<Integer, Double> shownHp = new HashMap<>();
    private final Map<Integer, Projectile> knownProjectiles = new HashMap<>();
    private final List<Corpse> corpses = new ArrayList<>();
    private final List<Vanishing> vanishing = new ArrayList<>();
    private final List<SpawnRing> spawnRings = new ArrayList<>();
    private final Deque<TitleCard> titles = new ArrayDeque<>();
    private final List<DungeonPainter.Light> dynamicLights = new ArrayList<>();

    private long lastNanos;
    private double time;
    private double camX = Double.NaN;
    private double camY = Double.NaN;
    private double shake;
    private double hurtFlash;
    private double ambientBudget;
    private double bossShownHp = 1;
    private double bossBarAlpha;

    private Arena lastArena;
    private ArenaState lastArenaState;
    private boolean bossAnnounced;
    private TitleCard currentTitle;

    private double outroStart = -1;
    private boolean outroVictory;
    private Runnable outroDone;

    private record Corpse(EntityAnimator animator, double x, double y) {
    }

    private record Vanishing(Entity entity, double start) {
    }

    private record SpawnRing(double x, double y, double start, Color color) {
    }

    private record TitleCard(String title, String subtitle, Color color, double duration, double[] start) {
    }

    private record DrawItem(double depth, Runnable draw) {
    }

    /**
     * Creates a new GameRenderer.
     * @param canvas canvas
     * @param level the level this belongs to
     * @param localHero local hero
     */
    public GameRenderer(Canvas canvas, Level level, Hero localHero) {
        this.canvas = canvas;
        this.level = level;
        this.gc = canvas.getGraphicsContext2D();
        this.localHero = localHero;
        gameManager = GameManager.getInstance();
        painter = level == null ? null : new DungeonPainter(level);
        if (level != null) {
            lastArena = level.getCurArena();
            lastArenaState = lastArena == null ? null : lastArena.getState();
            showTitle("Into the Depths", "Clear the arenas and slay what waits below", GOLD);
        }
    }

    @Override
    public void handle(long now) {
        if (level == null || localHero == null) {
            return;
        }
        double dt = lastNanos == 0 ? 1 / 60.0 : Math.min(MAX_DT, (now - lastNanos) / 1e9);
        lastNanos = now;
        render(dt);

        GameController controller = gameManager.getGameController();
        if (controller != null) {
            controller.updateHealthBar(localHero.getHp(), localHero.getMaxHP());
            controller.setGold(localHero.getGold());
        }
        if (outroDone != null && time - outroStart > OUTRO_TIME) {
            Runnable done = outroDone;
            outroDone = null;
            done.run();
        }
    }

    /**
     * Shows the end-of-level screen, then runs the callback.
     * @param victory whether the level was won
     * @param onDone what to do once the sequence ends
     */
    public void playOutro(boolean victory, Runnable onDone) {
        outroVictory = victory;
        outroStart = time;
        outroDone = onDone;
    }

    /**
     * Draws one frame.
     * @param dt seconds since the previous frame
     */
    public void render(double dt) {
        double viewW = canvas.getWidth();
        double viewH = canvas.getHeight();
        if (viewW <= 0 || viewH <= 0) {
            return;
        }
        time += dt;
        gc.setImageSmoothing(false);

        List<Entity> entities = snapshot(level.getAllEntities());
        List<Projectile> projectiles = snapshot(level.getProjectiles());
        trackEntities(entities, dt);
        trackProjectiles(projectiles);
        trackArena();
        emitAmbient(dt, viewW, viewH);
        effects.update(dt);
        updateCamera(dt, viewW, viewH);

        double shakeX = (random.nextDouble() - 0.5) * 2 * shake;
        double shakeY = (random.nextDouble() - 0.5) * 2 * shake;
        shake *= Math.exp(-9 * dt);
        hurtFlash = Math.max(0, hurtFlash - dt * 1.6);
        double cx = Math.round(camX + shakeX);
        double cy = Math.round(camY + shakeY);

        gc.setFill(Color.rgb(5, 3, 10));
        gc.fillRect(0, 0, viewW, viewH);
        painter.drawTiles(gc, cx, cy, viewW, viewH, time);
        drawSpawnRings(cx, cy);
        drawWorldObjects(entities, cx, cy);
        drawProjectiles(projectiles, cx, cy);
        drawUltimates(entities, cx, cy);
        effects.drawParticles(gc, cx, cy);
        painter.drawLighting(gc, cx, cy, viewW, viewH, localHero.getX() - cx, localHero.getY() - cy, time, dynamicLights);

        drawHealthBars(entities, cx, cy);
        effects.drawTexts(gc, cx, cy);
        drawChestPrompt(cx, cy);
        drawBossBar(dt, viewW);
        drawVignette(viewW, viewH);
        drawTitle(viewW, viewH);
        drawIntroOutro(viewW, viewH);
    }

    // ------------------------------------------------------------------ tracking

    private static <T> List<T> snapshot(Collection<T> source) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                List<T> copy = new ArrayList<>(source);
                copy.removeIf(java.util.Objects::isNull);
                return copy;
            } catch (RuntimeException concurrentChange) {
                // the network thread modified the list, try again
            }
        }
        return List.of();
    }

    private void trackEntities(List<Entity> entities, double dt) {
        Set<Integer> seen = new HashSet<>();
        for (Entity e : entities) {
            seen.add(e.getId());
            known.put(e.getId(), e);
            SpriteSet sprites = Sprites.forImg(e.getImgId());
            if (sprites == null) {
                if (!appeared.containsKey(e.getId())) {
                    appeared.put(e.getId(), time < 0.5 ? -10.0 : time);
                    onStaticAppeared(e);
                }
                continue;
            }
            EntityAnimator animator = animators.get(e.getId());
            if (animator == null) {
                boolean summoned = e instanceof Mob && time > 0.5;
                animator = new EntityAnimator(sprites, summoned);
                animators.put(e.getId(), animator);
                if (summoned) {
                    onSummoned(e);
                }
            }
            EntityAnimator.Changes changes = animator.observe(e, dt);
            if (changes.hpDelta() < 0) {
                onDamaged(e, -changes.hpDelta());
            } else if (changes.hpDelta() > 0 && e instanceof Hero) {
                effects.text(e.getX(), e.getY() - 20, "+" + changes.hpDelta(), Color.rgb(120, 255, 140), 20);
                effects.burst(e.getX(), e.getY(), Color.rgb(120, 255, 140), 14, 90, 0.8, -60, true);
            }
            if (changes.attacked()) {
                onAttack(e);
            }
        }

        Iterator<Map.Entry<Integer, Entity>> it = known.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Entity> entry = it.next();
            if (seen.contains(entry.getKey())) {
                continue;
            }
            it.remove();
            onRemoved(entry.getValue());
        }

        for (Corpse corpse : corpses) {
            corpse.animator().tick(dt);
        }
        corpses.removeIf(c -> c.animator().isFinished());
        vanishing.removeIf(v -> time - v.start() > RISE_TIME);
        spawnRings.removeIf(r -> time - r.start() > SPAWN_RING_TIME);
    }

    private void onStaticAppeared(Entity e) {
        if (appeared.get(e.getId()) < 0) {
            return;
        }
        if (e instanceof Grate) {
            effects.burst(e.getX(), e.getY() + Level.TILE_SIZE / 2.0, Color.rgb(90, 80, 100), 6, 60, 0.6, 120, false);
            shake = Math.max(shake, 3);
        } else if (e instanceof DroppedGold) {
            effects.burst(e.getX(), e.getY(), GOLD, 6, 80, 0.4, 200, true);
        }
    }

    private void onSummoned(Entity e) {
        Color color = e instanceof Boss ? Color.rgb(255, 80, 60) : Color.rgb(170, 110, 255);
        spawnRings.add(new SpawnRing(e.getX(), e.getY() + e.getHalfHitBox() * 0.6, time, color));
        effects.burst(e.getX(), e.getY() + e.getHalfHitBox() * 0.5, color, 18, 110, 0.7, -80, true);
    }

    private void onDamaged(Entity e, int amount) {
        lastHurt.put(e.getId(), time);
        effects.burst(e.getX(), e.getY(), bloodColor(e), Math.min(22, 5 + amount / 4), 150, 0.55, 420, false);
        boolean isLocal = e == localHero;
        Color textColor = isLocal ? Color.rgb(255, 90, 90) : Color.rgb(255, 235, 170);
        double size = Math.min(34, 18 + amount / 8.0);
        effects.text(e.getX(), e.getY() - 10, String.valueOf(amount), textColor, size);
        if (isLocal) {
            shake = Math.max(shake, 5 + Math.min(9, amount / 12.0));
            hurtFlash = 0.6;
        } else if (e instanceof Boss) {
            shake = Math.max(shake, 2);
        }
    }

    private void onAttack(Entity e) {
        if (e.getImgId() == ImgId.ANYSEK) {
            shake = Math.max(shake, 6);
            effects.burst(e.getX(), e.getY() + 40, Color.rgb(120, 90, 70), 26, 220, 0.6, 300, false);
        } else if (e.getImgId() == ImgId.DWIGHT) {
            effects.burst(e.getX(), e.getY(), Color.rgb(90, 170, 255), 16, 160, 0.5, 0, true);
        } else if (e instanceof Hero) {
            double dx = e.getLookX() - e.getX();
            double dy = e.getLookY() - e.getY();
            double len = Math.max(1, Math.hypot(dx, dy));
            effects.burst(e.getX() + dx / len * 28, e.getY() + dy / len * 28, Color.rgb(220, 230, 255), 5, 120, 0.2, 0, true);
        }
    }

    private void onRemoved(Entity gone) {
        appeared.remove(gone.getId());
        shownHp.remove(gone.getId());
        lastHurt.remove(gone.getId());
        EntityAnimator animator = animators.remove(gone.getId());
        if (animator != null) {
            animator.startDeath();
            corpses.add(new Corpse(animator, gone.getX(), gone.getY()));
            effects.burst(gone.getX(), gone.getY(), bloodColor(gone), 26, 190, 0.7, 380, false);
            effects.burst(gone.getX(), gone.getY(), Color.rgb(190, 170, 255), 10, 60, 1.2, -90, true);
            if (gone instanceof Boss) {
                shake = Math.max(shake, 14);
                effects.burst(gone.getX(), gone.getY(), GOLD, 60, 320, 1.4, 150, true);
                showTitle("Boss Slain", "The way out lies to the east", GOLD);
            }
            return;
        }
        if (gone instanceof DroppedGold gold) {
            effects.burst(gone.getX(), gone.getY(), GOLD, 12, 140, 0.5, 300, true);
            effects.text(gone.getX(), gone.getY() - 6, "+" + gold.getValue() + "g", GOLD, 20);
        } else if (gone instanceof Grate) {
            vanishing.add(new Vanishing(gone, time));
            effects.burst(gone.getX(), gone.getY() + Level.TILE_SIZE / 2.0, Color.rgb(90, 80, 100), 6, 60, 0.6, 120, false);
        }
    }

    private Color bloodColor(Entity e) {
        ImgId id = e.getImgId();
        if (id == null) {
            return BLOOD;
        }
        return switch (id) {
            case GHOST -> Color.rgb(170, 235, 255);
            case ZOMBIE -> Color.rgb(90, 110, 50);
            case DWIGHT -> Color.rgb(70, 140, 255);
            default -> BLOOD;
        };
    }

    private void trackProjectiles(List<Projectile> projectiles) {
        dynamicLights.clear();
        Set<Integer> seen = new HashSet<>();
        for (Projectile p : projectiles) {
            seen.add(p.getId());
            knownProjectiles.put(p.getId(), p);
            ImgId id = p.getImgId();
            if (id == ImgId.FIREBALL) {
                effects.drift(p.getX(), p.getY(), Color.rgb(255, 130, 40), 8, 20, 0.35, true);
                dynamicLights.add(new DungeonPainter.Light(p.getX(), p.getY(), 70, Color.rgb(255, 130, 40, 0.35), 1, p.getId()));
            } else if (id == ImgId.WATERBALL) {
                effects.drift(p.getX(), p.getY(), Color.rgb(90, 170, 255), 12, 10, 0.4, true);
                dynamicLights.add(new DungeonPainter.Light(p.getX(), p.getY(), 80, Color.rgb(70, 140, 255, 0.30), 0.5, p.getId()));
            } else if (random.nextInt(3) == 0) {
                effects.drift(p.getX(), p.getY(), Color.rgb(200, 200, 210, 0.6), 2, 0, 0.25, false);
            }
        }
        Iterator<Map.Entry<Integer, Projectile>> it = knownProjectiles.entrySet().iterator();
        while (it.hasNext()) {
            Projectile p = it.next().getValue();
            if (seen.contains(p.getId())) {
                continue;
            }
            it.remove();
            ImgId id = p.getImgId();
            if (id == ImgId.FIREBALL) {
                effects.burst(p.getX(), p.getY(), Color.rgb(255, 150, 50), 16, 170, 0.45, 0, true);
            } else if (id == ImgId.WATERBALL) {
                effects.burst(p.getX(), p.getY(), Color.rgb(110, 180, 255), 18, 180, 0.5, 350, false);
            } else {
                effects.burst(p.getX(), p.getY(), Color.rgb(140, 100, 60), 5, 80, 0.3, 300, false);
            }
        }
        for (Entity e : known.values()) {
            if (e instanceof Hero hero && hero.isUltimateActive()) {
                dynamicLights.add(new DungeonPainter.Light(hero.getX(), hero.getY(), 160, Color.rgb(150, 190, 255, 0.28), 0.6, 3));
            }
        }
    }

    private void trackArena() {
        Arena arena = level.getCurArena();
        ArenaState state = arena == null ? null : arena.getState();
        if (arena != lastArena || state != lastArenaState) {
            if (lastArena != null && arena != lastArena && lastArena.getState() == ArenaState.CLEARED) {
                showTitle("Arena Cleared", "The gates grind open", GOLD);
            } else if (arena != null && state == ArenaState.WAVE_FIGHT) {
                DungeonTheme theme = DungeonTheme.of(arena.getType());
                showTitle(theme.getTitle(), "The gates slam shut behind you", Color.rgb(235, 225, 200));
                shake = Math.max(shake, 5);
            } else if (arena != null && state == ArenaState.MINI_BOSS_FIGHT) {
                showTitle("A Champion Rises", "Defeat it to break the seal", Color.rgb(255, 150, 70));
            } else if (arena != null && state == ArenaState.CLEARED) {
                showTitle("Arena Cleared", "The gates grind open", GOLD);
            }
            lastArena = arena;
            lastArenaState = state;
        }
        Boss boss = level.getLevelBoss();
        if (boss != null && !bossAnnounced) {
            bossAnnounced = true;
            showTitle(bossName(boss), "Guardian of the Depths", Color.rgb(255, 90, 70));
            shake = Math.max(shake, 10);
        }
    }

    private void emitAmbient(double dt, double viewW, double viewH) {
        DungeonTheme theme = painter.themeAt(localHero.getX());
        if (theme.getAmbient() == null || Double.isNaN(camX)) {
            return;
        }
        ambientBudget += dt * 14;
        while (ambientBudget >= 1) {
            ambientBudget -= 1;
            double x = camX + random.nextDouble() * viewW;
            double y = camY + random.nextDouble() * viewH;
            effects.drift(x, y, theme.getAmbient(), 0, 14, 3.5, true);
        }
    }

    private void updateCamera(double dt, double viewW, double viewH) {
        double worldW = level.getWidth() * Level.TILE_SIZE;
        double worldH = level.getHeight() * Level.TILE_SIZE;
        double targetX = clamp(localHero.getX() - viewW / 2, 0, Math.max(0, worldW - viewW));
        double targetY = clamp(localHero.getY() - viewH / 2, 0, Math.max(0, worldH - viewH));
        if (Double.isNaN(camX)) {
            camX = targetX;
            camY = targetY;
            return;
        }
        double follow = 1 - Math.exp(-CAMERA_STIFFNESS * dt);
        camX += (targetX - camX) * follow;
        camY += (targetY - camY) * follow;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // ------------------------------------------------------------------ world drawing

    private void drawSpawnRings(double cx, double cy) {
        gc.setGlobalBlendMode(BlendMode.ADD);
        for (SpawnRing ring : spawnRings) {
            double t = (time - ring.start()) / SPAWN_RING_TIME;
            double r = 10 + 34 * Math.sqrt(t);
            Color c = ring.color();
            gc.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.8 * (1 - t)));
            gc.setLineWidth(3);
            gc.strokeOval(ring.x() - cx - r, ring.y() - cy - r * 0.45, r * 2, r * 0.9);
            gc.setFill(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.25 * (1 - t)));
            gc.fillOval(ring.x() - cx - r * 0.7, ring.y() - cy - r * 0.3, r * 1.4, r * 0.6);
        }
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
    }

    private void drawWorldObjects(List<Entity> entities, double cx, double cy) {
        List<DrawItem> items = new ArrayList<>();
        for (Entity e : entities) {
            if (!e.isAlive() && !(e instanceof Hero)) {
                continue;
            }
            double sx = e.getX() - cx;
            double sy = e.getY() - cy;
            if (sx < -200 || sy < -200 || sx > canvas.getWidth() + 200 || sy > canvas.getHeight() + 200) {
                continue;
            }
            EntityAnimator animator = animators.get(e.getId());
            if (animator != null) {
                items.add(new DrawItem(e.getY(), () -> {
                    animator.drawShadow(gc, sx, sy);
                    animator.draw(gc, sx, sy);
                }));
            } else {
                items.add(new DrawItem(e.getY() - (e instanceof DroppedGold ? 20 : 0), () -> drawStatic(e, sx, sy)));
            }
        }
        for (Corpse corpse : corpses) {
            double sx = corpse.x() - cx;
            double sy = corpse.y() - cy;
            items.add(new DrawItem(corpse.y() - 30, () -> {
                corpse.animator().drawShadow(gc, sx, sy);
                corpse.animator().draw(gc, sx, sy);
            }));
        }
        for (Vanishing v : vanishing) {
            double progress = 1 - (time - v.start()) / RISE_TIME;
            items.add(new DrawItem(v.entity().getY(), () -> drawGrate(v.entity().getX() - cx, v.entity().getY() - cy, progress)));
        }
        items.sort(Comparator.comparingDouble(DrawItem::depth));
        for (DrawItem item : items) {
            item.draw().run();
        }
    }

    private void drawStatic(Entity e, double sx, double sy) {
        double since = time - appeared.getOrDefault(e.getId(), -10.0);
        double t = Level.TILE_SIZE;
        switch (e) {
            case Grate ignored -> drawGrate(sx, sy, Math.min(1, since / RISE_TIME));
            case Chest chest -> drawChest(chest, sx, sy);
            case DroppedGold ignored -> {
                double pop = since < 0.3 ? easeOutBack(since / 0.3) : 1;
                double bob = Math.sin(time * 4 + e.getId()) * 2.5;
                shadow(sx, sy + t * 0.4, t * 0.5 * pop);
                double size = t * pop;
                gc.drawImage(ImgLoader.get(ImgId.GOLD), sx - size / 2, sy - size / 2 + bob - 4, size, size);
                if (random.nextInt(90) == 0) {
                    effects.drift(e.getX() + (random.nextDouble() - 0.5) * 14, e.getY() - 6, Color.rgb(255, 240, 170), 2, 20, 0.5, true);
                }
            }
            case Fence ignored -> {
                double pop = since < 0.35 ? easeOutBack(since / 0.35) : 1;
                shadow(sx, sy + t * 0.45, t * 0.9);
                gc.drawImage(ImgLoader.sheet(MAP + "dungeon/Barricade.png"), sx - t / 2, sy + t / 2 - t * pop, t, t * pop);
            }
            default -> {
                ImgId imgId = e.getImgId();
                if (imgId == null) {
                    return;
                }
                double w = t * imgId.getScale();
                gc.drawImage(ImgLoader.get(imgId), 0, 0, imgId.getFrameWidth(), imgId.getFrameHeight(), sx - w / 2, sy - w / 2, w, w);
            }
        }
    }

    private void drawChest(Chest chest, double sx, double sy) {
        double t = Level.TILE_SIZE;
        shadow(sx, sy + t * 0.42, t * 0.9);
        Image img = chest.isOpened() ? ImgLoader.sheet(MAP + "Treasure Chest open 32x32.png") : ImgLoader.get(ImgId.CHEST);
        double size = t * 1.15;
        gc.drawImage(img, sx - size / 2, sy - size / 2 - 3, size, size);
        if (!chest.isOpened() && random.nextInt(70) == 0) {
            effects.drift(chest.getX() + (random.nextDouble() - 0.5) * 20, chest.getY() - 8, Color.rgb(255, 230, 150), 4, 25, 0.6, true);
        }
    }

    private void drawGrate(double sx, double sy, double progress) {
        if (progress <= 0) {
            return;
        }
        double t = Level.TILE_SIZE;
        double p = easeOutBack(Math.max(0, Math.min(1, progress)));
        double h = Math.min(t, t * p);
        Image bars = ImgLoader.sheet(MAP + "dungeon/Portcullis.png");
        shadow(sx, sy + t * 0.45, t * 0.9 * Math.min(1, progress));
        gc.drawImage(bars, 0, 16 - 16 * h / t, 16, 16 * h / t, sx - t / 2, sy + t / 2 - h, t, h);
    }

    private void shadow(double x, double y, double width) {
        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        gc.fillOval(x - width / 2, y - width * 0.18, width, width * 0.36);
    }

    private static double easeOutBack(double t) {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    }

    private void drawProjectiles(List<Projectile> projectiles, double cx, double cy) {
        for (Projectile p : projectiles) {
            ImgId imgId = p.getImgId();
            double drawX = p.getX() - cx;
            double drawY = p.getY() - cy;
            double width = imgId.getWidth() * imgId.getScale();
            double height = imgId.getHeight() * imgId.getScale();
            if (drawX + width < 0 || drawY + height < 0 || drawX - width > canvas.getWidth() || drawY - height > canvas.getHeight()) {
                continue;
            }
            Image image = ImgLoader.get(imgId);
            double scale = 1;
            if (imgId == ImgId.FIREBALL && ((int) (time * 12 + p.getId()) % 2 == 1)) {
                image = ImgLoader.sheet(PROJECTILES + "Fireball2.png");
            } else if (imgId == ImgId.WATERBALL) {
                scale = 1 + 0.12 * Math.sin(time * 14 + p.getId());
            }
            double angle = Math.toDegrees(Math.atan2(p.getDeltaY(), p.getDeltaX()));
            gc.save();
            gc.translate(drawX, drawY);
            gc.rotate(angle);
            gc.scale(scale, scale);
            gc.drawImage(image, 0, 0, imgId.getFrameWidth(), imgId.getFrameHeight(), -width / 2, -height / 2, width, height);
            gc.restore();
        }
    }

    private void drawUltimates(List<Entity> entities, double cx, double cy) {
        for (Entity e : entities) {
            if (!(e instanceof Hero hero) || !hero.isUltimateActive()) {
                continue;
            }
            double radius = Warrior.B_STORM_RADIUS_MUL * hero.getReach();
            double x = hero.getX() - cx;
            double y = hero.getY() - cy;
            double spin = time * 540;
            gc.setGlobalBlendMode(BlendMode.ADD);
            gc.setLineWidth(4);
            for (int i = 0; i < 3; i++) {
                double r = radius * (0.45 + 0.25 * i);
                gc.setStroke(Color.rgb(170, 200, 255, 0.55 - i * 0.12));
                gc.strokeArc(x - r, y - r, r * 2, r * 2, spin + i * 120, 70, ArcType.OPEN);
                gc.strokeArc(x - r, y - r, r * 2, r * 2, spin + i * 120 + 180, 70, ArcType.OPEN);
            }
            gc.setStroke(Color.rgb(140, 170, 255, 0.18));
            gc.setLineWidth(2);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
            gc.setGlobalBlendMode(BlendMode.SRC_OVER);
            for (int i = 0; i < 3; i++) {
                effects.swirl(hero.getX(), hero.getY(), radius, Color.rgb(180, 210, 255));
            }
        }
    }

    // ------------------------------------------------------------------ HUD

    private void drawHealthBars(List<Entity> entities, double cx, double cy) {
        for (Entity e : entities) {
            if (!(e instanceof Mob) || e instanceof Boss || e.getMaxHP() <= 0) {
                continue;
            }
            Double hurt = lastHurt.get(e.getId());
            if (hurt == null || time - hurt > HP_BAR_TIME) {
                continue;
            }
            double ratio = Math.max(0, (double) e.getHp() / e.getMaxHP());
            double shown = shownHp.getOrDefault(e.getId(), 1.0);
            shown = Math.max(ratio, shown - 0.02);
            shownHp.put(e.getId(), shown);
            double alpha = Math.min(1, (HP_BAR_TIME - (time - hurt)) / 0.5);
            double w = 36;
            double x = e.getX() - cx - w / 2;
            double y = e.getY() - cy - e.getHalfHitBox() - 22;
            gc.setGlobalAlpha(alpha);
            gc.setFill(Color.rgb(10, 5, 15, 0.85));
            gc.fillRect(x - 1, y - 1, w + 2, 6);
            gc.setFill(Color.rgb(255, 240, 220));
            gc.fillRect(x, y, w * shown, 4);
            gc.setFill(Color.rgb(200, 40, 50));
            gc.fillRect(x, y, w * ratio, 4);
            gc.setGlobalAlpha(1);
        }
    }

    private void drawChestPrompt(double cx, double cy) {
        Chest nearest = null;
        double best = CHEST_RANGE * CHEST_RANGE;
        for (Entity e : known.values()) {
            if (e instanceof Chest chest) {
                double dx = chest.getX() - localHero.getX();
                double dy = chest.getY() - localHero.getY();
                double d = dx * dx + dy * dy;
                if (d < best) {
                    best = d;
                    nearest = chest;
                }
            }
        }
        if (nearest == null) {
            return;
        }
        String text = nearest.isOpened() ? "[F] Open" : "[F] Unlock  ·  " + Chest.PRICE_TO_OPEN_CHEST + " gold";
        double x = nearest.getX() - cx;
        double y = nearest.getY() - cy - 34 + Math.sin(time * 4) * 2;
        gc.setFont(PROMPT_FONT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setLineWidth(3);
        gc.setStroke(Color.rgb(10, 5, 15, 0.9));
        gc.strokeText(text, x, y);
        gc.setFill(nearest.isOpened() ? Color.rgb(230, 225, 210) : GOLD);
        gc.fillText(text, x, y);
    }

    private String bossName(Boss boss) {
        return boss.getImgId() == ImgId.DWIGHT ? "Dwight the Leviathan" : "Anysek the Behemoth";
    }

    private void drawBossBar(double dt, double viewW) {
        Boss boss = level.getLevelBoss();
        boolean present = boss != null && known.containsKey(boss.getId());
        bossBarAlpha = clamp(bossBarAlpha + (present ? dt : -dt) * 2, 0, 1);
        if (boss == null || bossBarAlpha <= 0) {
            return;
        }
        double ratio = present ? Math.max(0, (double) boss.getHp() / Math.max(1, boss.getMaxHP())) : 0;
        bossShownHp = Math.max(ratio, bossShownHp - dt * 0.35);
        double w = Math.min(560, viewW * 0.5);
        double x = (viewW - w) / 2;
        double y = 58;
        gc.setGlobalAlpha(bossBarAlpha);
        gc.setFont(BOSS_FONT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.BOTTOM);
        gc.setLineWidth(4);
        gc.setStroke(Color.rgb(10, 5, 15, 0.9));
        gc.strokeText(bossName(boss), viewW / 2, y - 4);
        gc.setFill(Color.rgb(235, 220, 190));
        gc.fillText(bossName(boss), viewW / 2, y - 4);
        gc.setFill(Color.rgb(10, 5, 15, 0.85));
        gc.fillRect(x - 3, y - 3, w + 6, 18);
        gc.setFill(Color.rgb(255, 235, 200));
        gc.fillRect(x, y, w * bossShownHp, 12);
        gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(230, 60, 60)), new Stop(1, Color.rgb(120, 10, 20))));
        gc.fillRect(x, y, w * ratio, 12);
        gc.setStroke(Color.rgb(160, 120, 60));
        gc.setLineWidth(1.5);
        gc.strokeRect(x - 3, y - 3, w + 6, 18);
        gc.setGlobalAlpha(1);
    }

    private void drawVignette(double viewW, double viewH) {
        double ratio = localHero.getMaxHP() > 0 ? (double) localHero.getHp() / localHero.getMaxHP() : 1;
        double lowHp = ratio < 0.3 && localHero.getHp() > 0 ? 0.25 + 0.2 * (0.5 + 0.5 * Math.sin(time * 6)) : 0;
        double intensity = Math.max(hurtFlash, lowHp);
        if (intensity <= 0) {
            return;
        }
        RadialGradient vignette = new RadialGradient(0, 0, viewW / 2, viewH / 2, Math.max(viewW, viewH) * 0.7, false,
                CycleMethod.NO_CYCLE,
                new Stop(0.55, Color.rgb(160, 0, 10, 0)),
                new Stop(1, Color.rgb(160, 0, 10, Math.min(0.8, intensity))));
        gc.setFill(vignette);
        gc.fillRect(0, 0, viewW, viewH);
    }

    private void showTitle(String title, String subtitle, Color color) {
        titles.add(new TitleCard(title, subtitle, color, TITLE_TIME, new double[]{-1}));
    }

    private void drawTitle(double viewW, double viewH) {
        if (currentTitle == null || time - currentTitle.start()[0] > currentTitle.duration()) {
            currentTitle = titles.poll();
            if (currentTitle == null) {
                return;
            }
            currentTitle.start()[0] = time;
        }
        double t = time - currentTitle.start()[0];
        double in = Math.min(1, t / 0.45);
        double out = Math.min(1, Math.max(0, (currentTitle.duration() - t) / 0.7));
        double alpha = Math.min(in, out);
        double y = viewH * 0.28 + (1 - easeOutBack(in)) * 20 - (1 - out) * 12;
        double spread = 60 + 180 * easeOutBack(in);

        gc.setGlobalAlpha(alpha);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(TITLE_FONT);
        gc.setLineWidth(6);
        gc.setStroke(Color.rgb(8, 4, 12, 0.85));
        gc.strokeText(currentTitle.title(), viewW / 2, y);
        gc.setFill(currentTitle.color());
        gc.fillText(currentTitle.title(), viewW / 2, y);

        gc.setFill(new LinearGradient(viewW / 2 - spread, 0, viewW / 2 + spread, 0, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT), new Stop(0.5, currentTitle.color()), new Stop(1, Color.TRANSPARENT)));
        gc.fillRect(viewW / 2 - spread, y + 40, spread * 2, 2);

        gc.setFont(SUBTITLE_FONT);
        gc.setLineWidth(4);
        gc.strokeText(currentTitle.subtitle(), viewW / 2, y + 62);
        gc.setFill(Color.rgb(225, 215, 195));
        gc.fillText(currentTitle.subtitle(), viewW / 2, y + 62);
        gc.setGlobalAlpha(1);
    }

    private void drawIntroOutro(double viewW, double viewH) {
        if (time < INTRO_TIME) {
            gc.setFill(Color.rgb(0, 0, 0, 1 - time / INTRO_TIME));
            gc.fillRect(0, 0, viewW, viewH);
        }
        if (outroStart < 0) {
            return;
        }
        double t = time - outroStart;
        gc.setFill(Color.rgb(0, 0, 0, Math.min(0.75, t / 1.2 * 0.75)));
        gc.fillRect(0, 0, viewW, viewH);

        double in = Math.min(1, t / 0.9);
        String text = outroVictory ? "Victory" : "You Died";
        Color color = outroVictory ? GOLD : Color.rgb(200, 30, 40);
        gc.setGlobalAlpha(in);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.save();
        gc.translate(viewW / 2, viewH * 0.42);
        double scale = 0.8 + 0.2 * easeOutBack(in);
        gc.scale(scale, scale);
        gc.setFont(Font.font("Pirata One", 110));
        gc.setLineWidth(8);
        gc.setStroke(Color.rgb(8, 4, 12));
        gc.strokeText(text, 0, 0);
        gc.setFill(color);
        gc.fillText(text, 0, 0);
        gc.restore();
        gc.setFont(SUBTITLE_FONT);
        gc.setFill(Color.rgb(225, 215, 195));
        gc.fillText(outroVictory ? "The dungeon falls silent" : "The darkness claims another soul", viewW / 2, viewH * 0.42 + 80);
        gc.setGlobalAlpha(1);

        double fadeStart = OUTRO_TIME - 0.8;
        if (t > fadeStart) {
            gc.setFill(Color.rgb(0, 0, 0, Math.min(1, (t - fadeStart) / 0.8)));
            gc.fillRect(0, 0, viewW, viewH);
        }
    }
}
