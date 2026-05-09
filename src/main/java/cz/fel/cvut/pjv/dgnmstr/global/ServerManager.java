package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.dungeon.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.*;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.network.ServerSpawner;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;


import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerManager {
    private LevelController lc;
    private final DungeonGenerator generator;
    private ServerNetworkManager snm;
    private LevelState lastLevelState;
    private ArenaState lastArenaState;
    private ScheduledExecutorService updateExecutor;
    private ScheduledFuture<?> updateTask;
    private volatile boolean running = false;
    private long seed;
    private Random random = new Random();



    public ServerManager(ServerNetworkManager manager) {
        generator = new DungeonGenerator();
        this.snm = manager;
    }
    public void generateNewLevel() {
        seed = random.nextLong();
        Level level = generator.generate(DifficultyStats.MEDIUM, seed);
        level.setSpawner(new ServerSpawner(level, new IDGenerator(), snm));
        lc = new LevelController(level);
    }

    public int addProfile(PlayerProfile profile) {
        Hero w1 = lc.getLevel().requestHeroSpawn(128, 128, profile);
        return w1.getId();
    }

    public void startLC() {
        if (running || lc == null) {
            return;
        }
        lc.startLevel();
        running = true;
        updateExecutor = Executors.newSingleThreadScheduledExecutor();
        updateTask = updateExecutor.scheduleAtFixedRate(this::tick, 0, 16, TimeUnit.MILLISECONDS);
        System.out.println("lc started");
    }

    public void stopLC() {
        running = false;
        snm.getTrackedEntityIds().clear();
        snm.getTrackedProjectileIds().clear();
        snm.getLastInputsByHeroId().clear();
        if (updateTask != null) {
            updateTask.cancel(false);
            updateTask = null;
        }
    }

    public void updateByProfile(int id, PlayerProfile profile) {
        if (lc.getLevel().getEntity(id) instanceof Hero hero) {
            hero.getProfile().mergeProfileChanges(profile);
            hero.syncStats(profile.getPlayerStats());
        }
    }

    public void applyInputs(int heroId, Inputs inputs) {
        if (lc == null || inputs == null) {
            return;
        }
        Entity entity = lc.getLevel().getEntity(heroId);
        if (entity instanceof Hero hero) {
            hero.setInputs(inputs);
            snm.getLastInputsByHeroId().put(heroId, inputs);
        }
    }


    private void tick() {
        if (!running || lc == null) {
            return;
        }

        double deltaTime = 0.016;
        lc.update(deltaTime);

        snm.sendArenaUpdates();
        snm.sendLevelUpdates();
        snm.sendEntityUpdates();
        snm.sendProjectileUpdates();

        if (lc.getLevel().getState() == LevelState.GAME_OVER ||
            lc.getLevel().getState() == LevelState.VICTORY) {
            stop();
        }
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

    public void stop() {
        updateExecutor.shutdownNow();
        stopLC();
    }

    public long getSeed() {
        return seed;
    }
}
