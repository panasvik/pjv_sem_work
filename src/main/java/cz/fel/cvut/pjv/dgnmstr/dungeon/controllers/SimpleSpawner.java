package cz.fel.cvut.pjv.dgnmstr.dungeon.controllers;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Projectile;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.Boss;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.BossStats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Warrior;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.entity.map.DroppedGold;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;
import cz.fel.cvut.pjv.dgnmstr.network.*;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

public class SimpleSpawner implements EntitySpawner {
    private final Level level;
    private final IDGenerator idGen;


    public SimpleSpawner(Level level, IDGenerator idGen, ServerNetworkManager networkServer) {
        this.level = level;
        this.idGen = idGen;
    }

    @Override
    public void spawnProjectile(Entity master, int radius, int damage, double deltaX, double deltaY, double velocity) {
        int newId = idGen.getNextId();

        Projectile p = new Projectile(newId, radius, master.getX(), master.getY(), master, damage, deltaX, deltaY, velocity);

        level.addProjectile(p);


    }

    @Override
    public void spawnGold(double x, double y, int amount) {
        int newId = idGen.getNextId();

        DroppedGold gold = new DroppedGold(newId, x, y, level, amount);
        level.addEntity(gold);


    }

    @Override
    public Mob spawnMob(double x, double y, MobStats stats, int mult) {
        int newId = idGen.getNextId();

        Mob mob = stats.spawnWithMult(newId, x, y, level, mult);
        level.addEntity(mob);


        return mob;
    }

    @Override
    public Boss spawnBoss(double x, double y, BossStats stats) {
        int newId = idGen.getNextId();

        Boss boss = stats.spawn(newId, x, y, level); // Предполагается, что у BossStats есть метод spawn
        level.addEntity(boss);


        return boss;
    }

    @Override
    public Grate spawnGrate(double x, double y) {
        int newId = idGen.getNextId();

        Grate grate = new Grate(newId, x, y, level);
        level.addEntity(grate);

        return grate;
    }
    @Override
    public Chest spawnChest(double x, double y, Inventory inv) {
        int newId = idGen.getNextId();

        Chest chest = new Chest(newId, x, y, level, inv);
        level.addEntity(chest);

        return chest;
    }

    @Override
    public Hero spawnHero(double x, double y, PlayerProfile profile) {
        int newId = idGen.getNextId();
        Hero w1 = new Warrior(newId, profile, x, y, level);
        level.addEntity(w1);
        return w1;
    }
}
