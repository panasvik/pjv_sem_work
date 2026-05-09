package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.EntitySpawner;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Projectile;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.Boss;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.BossStats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Warrior;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.entity.map.DroppedGold;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Fence;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;
import cz.fel.cvut.pjv.dgnmstr.global.ProjectileType;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.util.logging.Logger;

public class TestSpawner implements EntitySpawner{
    private final Level level;
    private final IDGenerator idGen;

    private static final Logger log = Logger.getLogger(TestSpawner.class.getName());

    public TestSpawner(Level level, IDGenerator idGen) {
        this.level = level;
        this.idGen = idGen;
    }

    @Override
    public void spawnProjectile(ProjectileType type, Entity master, int radius, int damage, double deltaX, double deltaY, double velocity) {
        int newId = idGen.getNextId();

        Projectile p = new Projectile(newId, type, radius, master.getCenterX(), master.getCenterY(), master, damage, deltaX, deltaY, velocity);

        level.addProjectile(p);

    }

    @Override
    public void spawnGold(double x, double y, int amount) {
        int newId = idGen.getNextId();

        DroppedGold gold = new DroppedGold(newId, x, y, level, amount);
        double half = Level.TILE_SIZE * gold.getImgId().getScale() / 2.0;
        gold.setPos(x - half, y - half);
        level.addEntity(gold);
    }

    @Override
    public Mob spawnMob(double x, double y, MobStats stats, int mult) {
        int newId = idGen.getNextId();

        Mob mob = stats.spawnWithMult(newId, x, y, level, mult);
        double half = Level.TILE_SIZE * mob.getImgId().getScale() / 2.0;
        mob.setPos(x - half, y - half);
        if (!level.isWalkable(mob.getCenterX(), mob.getCenterY(), mob.getHalfHitBox())) {
            return null;
        }
        level.addEntity(mob);
        return mob;
    }

    @Override
    public Boss spawnBoss(double x, double y, BossStats stats) {
        int newId = idGen.getNextId();

        Boss boss = stats.spawn(newId, x, y, level); // Предполагается, что у BossStats есть метод spawn
        double half = Level.TILE_SIZE * boss.getImgId().getScale() / 2.0;
        boss.setPos(x - half, y - half);
        level.addEntity(boss);
        return boss;
    }

    @Override
    public Grate spawnGrate(double x, double y) {
        int newId = idGen.getNextId();
        Grate grate = new Grate(newId, x, y, level);
        double half =  Level.TILE_SIZE * grate.getImgId().getScale() / 2.0;
        grate.setPos(x - half, y - half);
        level.addEntity(grate);
        return grate;
    }

    @Override
    public void spawnChest(double x, double y, Inventory inv) {
        int newId = idGen.getNextId();
        Chest chest = new Chest(newId, x, y, level, inv);
        level.addEntity(chest);
        level.addChest(chest);
        log.info("send spawning chest with id " + newId);
    }

    @Override
    public Hero spawnHero(double x, double y, PlayerProfile profile) {
        int newId = idGen.getNextId();
        Hero w1 = new Warrior(newId, profile, x, y, level);
        double half = Level.TILE_SIZE * w1.getImgId().getScale() / 2.0;
        w1.setPos(x - half, y - half);
        level.addPlayer(w1);
        return w1;
    }

    @Override
    public void spawnFence(double x, double y) {
        int newId= idGen.getNextId();
        Fence fence = new Fence(newId, x, y, level);
        level.addEntity(fence);
    }

}
