package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.*;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.map.*;
import cz.fel.cvut.pjv.dgnmstr.global.IDGenerator;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;


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
    private final ScheduledExecutorService updateExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> updateTask;
    private volatile boolean running = false;



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
        snm.getTrackedEntityIds().clear();
        snm.getTrackedProjectileIds().clear();
        snm.getLastInputsByHeroId().clear();
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
            snm.getLastInputsByHeroId().put(heroId, inputs);
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
        lc.update(deltaTime);

        snm.sendArenaUpdates();
        snm.sendLevelUpdates();
        snm.sendEntityUpdates();
        snm.sendProjectileUpdates();
    }


    public LevelController getLc() {
        return lc;
    }

    public LevelState getLastLevelState() {
        return lastLevelState;
    }

    public ArenaState getLastArenaState() {
        return lastArenaState;
    }



    public void setLastLevelState(LevelState lastLevelState) {
        this.lastLevelState = lastLevelState;
    }

    public void setLastArenaState(ArenaState lastArenaState) {
        this.lastArenaState = lastArenaState;
    }
}
