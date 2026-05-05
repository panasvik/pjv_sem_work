package cz.fel.cvut.pjv.dgnmstr.dungeon.controllers;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
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
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

/**
 * Реализация фабрики сущностей для Сервера.
 * Сервер имеет право генерировать уникальные ID и обязан рассылать
 * информацию о новых сущностях всем подключенным клиентам.
 */
public class ServerSpawner implements EntitySpawner {
    private final Level level;
    private final IDGenerator idGen;

    // Псевдо-класс для вашей сетевой логики.
    // Это должен быть класс, который умеет отправлять DGNPacket всем клиентам.
    private final ServerNetworkManager networkServer;

    public ServerSpawner(Level level, IDGenerator idGen, ServerNetworkManager networkServer) {
        this.level = level;
        this.idGen = idGen;
        this.networkServer = networkServer;
    }

    @Override
    public void spawnProjectile(Entity master, int radius, int damage, double deltaX, double deltaY, double velocity) {
        int newId = idGen.getNextId();

        Projectile p = new Projectile(newId, radius, master.getCenterX(), master.getCenterY(), master, damage, deltaX, deltaY, velocity);

        level.addProjectile(p);

        networkServer.broadcast(new DGNPacket(PacketType.SPAWN_PROJECTILE,
                new SpawnProjectilePayload(newId, radius, master.getCenterX(), master.getCenterY(), master.getId(), damage, deltaX, deltaY, velocity)
        ));
    }

    @Override
    public void spawnGold(double x, double y, int amount) {
        int newId = idGen.getNextId();

        DroppedGold gold = new DroppedGold(newId, x, y, level, amount);
        double half = Level.TILE_SIZE * gold.getImgId().getScale() / 2.0;
        gold.setPos(x - half, y - half);
        level.addEntity(gold);

        networkServer.broadcast(
                new DGNPacket(
                        PacketType.SPAWN_GOLD, new SpawnGoldPayload(newId, gold.getX(), gold.getY(), amount)
                ));

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

        networkServer.broadcast(new DGNPacket(PacketType.SPAWN_MOB,
                new SpawnMobPayload(newId, stats, mob.getX(), mob.getY(), mult)
        ));

        return mob;
    }

    @Override
    public Boss spawnBoss(double x, double y, BossStats stats) {
        int newId = idGen.getNextId();

        Boss boss = stats.spawn(newId, x, y, level); // Предполагается, что у BossStats есть метод spawn
        double half = Level.TILE_SIZE * boss.getImgId().getScale() / 2.0;
        boss.setPos(x - half, y - half);
        level.addEntity(boss);

        networkServer.broadcast(new DGNPacket(PacketType.SPAWN_BOSS,
                new SpawnBossPayload(newId, boss.getX(), boss.getY(), stats)));

        return boss;
    }

    @Override
    public Grate spawnGrate(double x, double y) {
        int newId = idGen.getNextId();
        Grate grate = new Grate(newId, x, y, level);
        double half =  Level.TILE_SIZE * grate.getImgId().getScale() / 2.0;
        grate.setPos(x - half, y - half);
        level.addEntity(grate);

        networkServer.broadcast(new DGNPacket(PacketType.SPAWN_GRATE,
                new SpawnGratePayload(newId, grate.getX(), grate.getY())));

        return grate;
    }

    @Override
    public Chest spawnChest(double x, double y, Inventory inv) {
        int newId = idGen.getNextId();

        Chest chest = new Chest(newId, x, y, level, inv);
        level.addEntity(chest);

        networkServer.broadcast(new DGNPacket(PacketType.SPAWN_CHEST,
                new SpawnChestPayload(newId, x, y)));

        return chest;
    }

    @Override
    public Hero spawnHero(double x, double y, PlayerProfile profile) {
        int newId = idGen.getNextId();
        Hero w1 = new Warrior(newId, profile, x, y, level);
        double half = Level.TILE_SIZE * w1.getImgId().getScale() / 2.0;
        w1.setPos(x - half, y - half);
        level.addPlayer(w1);
        networkServer.broadcast(new DGNPacket(PacketType.SPAWN_HERO, new
                SpawnHeroPayload(newId, w1.getX(), w1.getY(), profile)));
        return w1;
    }
}