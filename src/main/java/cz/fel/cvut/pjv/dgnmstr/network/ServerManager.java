package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.*;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.map.*;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerManager {
    private LevelController lc;
    private DungeonGenerator generator;
    private ServerNetworkManager snm;
    private long lastUpdateTimeNanos;
    private LevelState lastLevelState;
    private ArenaState lastArenaState;
    private final Set<Integer> trackedEntityIds = new HashSet<>();
    private final Set<Integer> trackedProjectileIds = new HashSet<>();
    private final Map<Integer, Inputs> lastInputsByHeroId = new ConcurrentHashMap<>();
    private final ScheduledExecutorService updateExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> updateTask;
    private volatile boolean running = false;
    private static final Inputs EMPTY_INPUTS =
            new Inputs(false, false, false, false, false, false, false, 0, 0);

    public ServerManager(ServerNetworkManager manager) {
        generator = new DungeonGenerator();
        this.snm = manager;
    }

    public void generateNewLevel(long seed) {
        Level level = generator.generate(DifficultyStats.MEDIUM, seed);
        level.setSpawner(new ServerSpawner(level, new IDGenerator(), snm));
        lc = new LevelController(level);
    }

    public int addProfile(PlayerProfile profile) {
        Hero w1 = lc.getLevel().requestHeroSpawn(64, 64, profile);
        return w1.getId();
    }

    public void startLC() {
        if (running || lc == null) {
            return;
        }
        lc.startLevel();
        running = true;
        updateTask = updateExecutor.scheduleAtFixedRate(this::tick, 0, 16, TimeUnit.MILLISECONDS);
        System.out.println("lc started");
    }

    public void stopLC() {
        running = false;
        lastUpdateTimeNanos = 0L;
        trackedEntityIds.clear();
        trackedProjectileIds.clear();
        lastInputsByHeroId.clear();
        if (updateTask != null) {
            updateTask.cancel(false);
            updateTask = null;
        }
    }

    public void applyInputs(int heroId, Inputs inputs) {
        if (lc == null || inputs == null) {
            return;
        }
        Entity entity = lc.getLevel().getEntityMap().get(heroId);
        if (entity instanceof Hero hero) {
            hero.setInputs(inputs);
            lastInputsByHeroId.put(heroId, inputs);
        }
    }

    private void broadcastSpawnForEntity(Entity entity) {
        if (entity instanceof Chest chest) {
            snm.broadcast(new DGNPacket(PacketType.SPAWN_CHEST,
                    new SpawnChestPayload(chest.getId(), chest.getX(), chest.getY())));
        } else if (entity instanceof Grate grate) {
            snm.broadcast(new DGNPacket(PacketType.SPAWN_GRATE,
                    new SpawnGratePayload(grate.getId(), grate.getX(), grate.getY())));
        } else if (entity instanceof Fence fence) {
            snm.broadcast(new DGNPacket(PacketType.SPAWN_FENCE,
                    new SpawnFencePayload(fence.getId(), fence.getX(), fence.getY())));
        } else if (entity instanceof DroppedGold gold) {
            snm.broadcast(new DGNPacket(PacketType.SPAWN_GOLD,
                    new SpawnGoldPayload(gold.getId(), gold.getX(), gold.getY(), gold.getValue())));
        }
    }

    private void tick() {
        if (!running || lc == null) {
            return;
        }
        long now = System.nanoTime();
        if (lastUpdateTimeNanos == 0L) {
            lastUpdateTimeNanos = now;
            return;
        }

        double deltaTime = (now - lastUpdateTimeNanos) / 1_000_000_000.0;
        lastUpdateTimeNanos = now;
        lc.update(deltaTime);

        Level level = lc.getLevel();
        LevelState currentLevelState = level.getState();
        if (lastLevelState != currentLevelState) {
            snm.broadcast(new DGNPacket(PacketType.LEVEL_STATE, new LevelStatePayload(currentLevelState)));
            lastLevelState = currentLevelState;
        }

        ArenaState currentArenaState = level.getCurArena() != null ? level.getCurArena().getState() : null;
        if (currentArenaState != null && lastArenaState != currentArenaState) {
            snm.broadcast(new DGNPacket(PacketType.ARENA_STATE, new ArenaStatePayload(currentArenaState)));
            lastArenaState = currentArenaState;
        }

        Set<Integer> currentEntityIds = new HashSet<>();
        for (Entity entity : level.getAllEntities()) {
            currentEntityIds.add(entity.getId());
            if (!trackedEntityIds.contains(entity.getId())) {
                broadcastSpawnForEntity(entity);
            }
            if (entity instanceof Hero hero) {
                Inputs inputs = lastInputsByHeroId.getOrDefault(hero.getId(), EMPTY_INPUTS);
                snm.broadcast(new DGNPacket(PacketType.UPDATE_HERO,
                        new UpdateHeroPayload(
                                hero.getId(),
                                hero.getX(),
                                hero.getY(),
                                hero.getHp(),
                                inputs.mouseX(),
                                inputs.mouseY(),
                                inputs.attack(),
                                inputs.ult(),
                                inputs.interact()
                        )));
                if (hero.getMaxHP() != hero.getProfile().getPlayerStats().hp()) {
                    snm.broadcast(new DGNPacket(PacketType.SYNC_STATS,
                            new SyncPlayerStatsPayload(hero.getId(), hero.getProfile().getPlayerStats())));
                }
            } else if (entity instanceof Mob mob) {
                snm.broadcast(new DGNPacket(PacketType.UPDATE_MOB,
                        new UpdateMobPayload(mob.getId(), mob.getX(), mob.getY(), mob.getHp())));
            }
        }

        for (Integer removedEntityId : trackedEntityIds) {
            if (!currentEntityIds.contains(removedEntityId)) {
                snm.broadcast(new DGNPacket(PacketType.ENTITY_REMOVE, new EntityRemovePayload(removedEntityId)));
            }
        }
        trackedEntityIds.clear();
        trackedEntityIds.addAll(currentEntityIds);

        Set<Integer> currentProjectileIds = new HashSet<>();
        for (Projectile projectile : level.getProjectiles()) {
            currentProjectileIds.add(projectile.getId());
            snm.broadcast(new DGNPacket(PacketType.UPDATE_PROJECTILE,
                    new UpdateProjectilePayload(
                            projectile.getId(),
                            projectile.getX(),
                            projectile.getY(),
                            projectile.isAlive()
                    )));
        }

        for (Integer removedProjectileId : trackedProjectileIds) {
            if (!currentProjectileIds.contains(removedProjectileId)) {
                snm.broadcast(new DGNPacket(PacketType.REMOVE_PROJECTILE, new EntityRemovePayload(removedProjectileId)));
            }
        }
        trackedProjectileIds.clear();
        trackedProjectileIds.addAll(currentProjectileIds);
    }
}
