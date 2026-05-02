package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.DungeonGenerator;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.LevelController;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.DifficultyStats;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Projectile;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.Boss;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Warrior;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.entity.map.DroppedGold;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Fence;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.ArenaStatePayload;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;

public class ClientManager {
    private LevelController lc;
    private Hero localHero;

    public ClientManager() {
    }

    public void makeNewLC(long seed) {
        DungeonGenerator generator = new DungeonGenerator();
        Level level = generator.generate(DifficultyStats.MEDIUM, seed);
        lc = new LevelController(level);
    }

    public void addPlayer(SpawnHeroPayload payload) {
        Hero h1 = new Warrior(payload.id(), payload.profile(), payload.x(), payload.y(), lc.getLevel());
        lc.getLevel().addPlayer(h1);
    }

    public void setLocalHero(int id) {
        localHero = (Hero) lc.getLevel().getEntityMap().get(id);
        System.out.println("local hero set");
        System.out.println(localHero);
    }

    public void spawnMob(SpawnMobPayload payload) {
        if (lc == null || lc.getLevel().getEntityMap().containsKey(payload.id())) return;
        Mob mob = payload.stats().spawnWithMult(payload.id(), payload.PosX(), payload.PosY(), lc.getLevel(), payload.mult());
        lc.getLevel().addEntity(mob);
    }

    public void spawnBoss(SpawnBossPayload payload) {
        if (lc == null || lc.getLevel().getEntityMap().containsKey(payload.id())) return;
        Boss boss = payload.stats().spawn(payload.id(), payload.x(), payload.y(), lc.getLevel());
        lc.getLevel().addEntity(boss);
        lc.getLevel().setLevelBoss(boss);
        lc.getLevel().setBossSpawned(true);
    }

    public void spawnProjectile(SpawnProjectilePayload payload) {
        if (lc == null || lc.getLevel().getProjectileMap().containsKey(payload.id())) return;
        Entity master = lc.getLevel().getEntityMap().get(payload.masterID());
        if (master == null) return;
        Projectile projectile = new Projectile(payload.id(), payload.radius(), payload.posX(), payload.posY(), master,
                payload.damage(), payload.deltaX(), payload.deltaY(), payload.velocity());
        lc.getLevel().addProjectile(projectile);
    }

    public void spawnChest(SpawnChestPayload payload) {
        if (lc == null || lc.getLevel().getEntityMap().containsKey(payload.id())) return;
        lc.getLevel().addEntity(new Chest(payload.id(), payload.x(), payload.y(), lc.getLevel(), new Inventory()));
    }

    public void spawnGrate(SpawnGratePayload payload) {
        if (lc == null || lc.getLevel().getEntityMap().containsKey(payload.id())) return;
        lc.getLevel().addEntity(new Grate(payload.id(), payload.x(), payload.y(), lc.getLevel()));
    }

    public void spawnFence(SpawnFencePayload payload) {
        if (lc == null || lc.getLevel().getEntityMap().containsKey(payload.id())) return;
        lc.getLevel().addEntity(new Fence(payload.id(), payload.x(), payload.y(), lc.getLevel()));
    }

    public void spawnGold(SpawnGoldPayload payload) {
        if (lc == null || lc.getLevel().getEntityMap().containsKey(payload.id())) return;
        lc.getLevel().addEntity(new DroppedGold(payload.id(), payload.x(), payload.y(), lc.getLevel(), payload.amount()));
    }

    public void updateHero(UpdateHeroPayload payload) {
        if (lc == null) return;
        Entity entity = lc.getLevel().getEntityMap().get(payload.id());
        if (!(entity instanceof Hero hero)) return;
        hero.setPos(payload.x(), payload.y());
        hero.setHp(payload.hp());
        hero.setLookTarget(payload.mouseX(), payload.mouseY());
        hero.setInputs(new Inputs(payload.isUlt(), payload.isInteract(), payload.isAttack(),
                false, false, false, false, payload.mouseX(), payload.mouseY()));
    }

    public void updateMob(UpdateMobPayload payload) {
        if (lc == null) return;
        Entity entity = lc.getLevel().getEntityMap().get(payload.id());
        if (!(entity instanceof Mob mob)) return;
        mob.setPos(payload.x(), payload.y());
        mob.setHp(payload.hp());
    }

    public void updateProjectile(UpdateProjectilePayload payload) {
        if (lc == null) return;
        Projectile projectile = lc.getLevel().getProjectileMap().get(payload.id());
        if (projectile == null) return;
        projectile.setPos(payload.x(), payload.y());
        projectile.setAlive(payload.isAlive());
    }

    public void removeEntity(EntityRemovePayload payload) {
        if (lc == null) return;
        Entity entity = lc.getLevel().getEntityMap().remove(payload.id());
        if (entity != null) {
            lc.getLevel().getAllEntities().remove(entity);
            if (entity instanceof Hero hero) {
                lc.getLevel().getPlayers().remove(hero);
            }
        }
    }

    public void removeProjectile(EntityRemovePayload payload) {
        if (lc == null) return;
        Projectile projectile = lc.getLevel().getProjectileMap().remove(payload.id());
        if (projectile != null) {
            lc.getLevel().getProjectiles().remove(projectile);
        }
    }

    public void updateLevelState(LevelStatePayload payload) {
        if (lc == null) return;
        lc.getLevel().setState(payload.state());
        if (payload.state() == LevelState.PLAYING) {
            lc.startLevel();
        }
    }

    public void updateArenaState(ArenaStatePayload payload) {
        if (lc == null || lc.getLevel().getCurArena() == null) return;
        lc.getLevel().getCurArena().setState(payload.state());
        if (payload.state() == ArenaState.CLEARED) lc.activateNextArena();
    }

    public void syncStats(SyncPlayerStatsPayload payload) {
        if (lc == null) return;
        Entity entity = lc.getLevel().getEntityMap().get(payload.id());
        if (!(entity instanceof Hero hero)) return;
        PlayerStats stats = payload.stats();
        hero.syncStats(stats);
    }

    public LevelController getLc() {
        return lc;
    }

    public Hero getLocalHero() {
        return localHero;
    }
}
