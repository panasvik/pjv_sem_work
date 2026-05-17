package cz.fel.cvut.pjv.dgnmstr.dungeon.controllers;

import cz.fel.cvut.pjv.dgnmstr.dungeon.*;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.BossSpawnRate;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.TilePosition;
import cz.fel.cvut.pjv.dgnmstr.entity.*;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.Boss;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.BossStats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Updatable;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.Projectile;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

import java.util.List;
import java.util.logging.Logger;

import static cz.fel.cvut.pjv.dgnmstr.dungeon.Level.TILE_SIZE;
import static cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState.VICTORY;
/**
 * Level controller.
 */
public class LevelController implements Updatable {
    private final Level level;
    private int curArenaIDX = 0;
    private ArenaController arenaController;
    private static final Logger log = Logger.getLogger(LevelController.class.getName());

    /**
     * Creates a new LevelController.
     *
     * @param level the level this belongs to
     */
    public LevelController(Level level) {
        this.level = level;
    }

    public void startLevel() {
        level.setState(LevelState.PLAYING);
        level.setPlayersAlive(level.getPlayers().size());
        log.info("players alive: " + level.getPlayersAlive());

        spawnChests();
        spawnFences();
        activateNextArena();
        log.info("Level started");
    }

    /**
     * Shifts state by one game tick.
     * @param deltaTime delta time
     */
    @Override
    public void update(double deltaTime) {
        updateEntities(deltaTime);
        updateProjectiles(deltaTime);
        checkPlayers();

        if (arenaController != null) {
            arenaController.update(deltaTime);
            checkArenaTransition();
        }
        checkProgress();
        checkLevelComplete();
    }


    /**
     * Shifts entities(updateEntities) and then projectiles(updateProjectiles) by one game tick.
     * @param deltaTime delta time
     */

    public void updateEntities(double deltaTime) {
        List<Entity> entities = level.getAllEntities();
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            e.update(deltaTime);
            if (!e.isAlive()) {
                level.removeEntity(e);
            }
        }



    }

    public void updateProjectiles(double deltaTime) {
        for (int i = level.getProjectiles().size() - 1; i >= 0; i--) {
            Projectile p = level.getProjectiles().get(i);
            p.update(deltaTime);
            if (!p.isAlive()) {
                level.removeProjectile(p);
            }
        }
    }

    private void checkPlayers() {
        for (Hero hero: level.getPlayers()) {
            checkPlayer(hero);
        }
    }


    private void checkPlayer(Hero player) {
        if (!player.isAlive()) {
            playerDies(player);
        }
    }

    public void playerDies(Hero deadPlayer) {
        deadPlayer.die();
        level.setPlayersAlive(countAlivePlayers());
        log.info("Player dead, active players:" + level.getPlayersAlive());

        if (level.getPlayersAlive() <= 0) {
            gameOver();
        } else {
            level.removeEntity(deadPlayer);
        }
    }

    private int countAlivePlayers() {
        int alivePlayers = 0;
        for (Hero hero : level.getPlayers()) {
            if (hero.isAlive()) {
                alivePlayers++;
            }
        }
        return alivePlayers;
    }

    private void spawnBoss() {
        Arena lastArena = level.getArenas().getLast();

        double random = Math.random();
        double sum = 0;
        BossStats selectedBoss = BossStats.ANYSEK_THE_BEHEMOTH;
        for (BossSpawnRate rate : level.getBossSpawnRate()) {
            sum += rate.chance();
            if (random <= sum) {
                selectedBoss = rate.type();
                break;
            }
        }

        double spawnX = lastArena.getCenterWorldX();
        double spawnY = lastArena.getCenterWorldY();

        Boss levelBoss = level.requestBossSpawn(spawnX, spawnY, selectedBoss);

        if (levelBoss != null) {
            level.setLevelBoss(levelBoss);
            level.setBossSpawned(true);
            log.info("FINAL BOSS AWAITS! " + levelBoss.getClass().getSimpleName() + " spawned in the center.");
        }
    }

    private void gameOver() {
        level.setState(LevelState.GAME_OVER);
        log.info("GAME OVER!");
    }

    private void checkLevelComplete() {
        if (level.getState() == LevelState.VICTORY) {
            log.info("VICTORY!");
        }
    }

    private void levelComplete() {
        level.setState(LevelState.VICTORY);
        log.info("VICTORY!");
    }

    public void checkProgress() {
        if (!level.isBossSpawned()) return;
        if (!level.getLevelBoss().isAlive()) {
            checkExit();
        }
    }

    public void checkExit(){
        List<Hero> players = level.getPlayers();
        for (Hero player : players) {
            if (player.isAlive() && level.isAtExit(player.getX(), player.getY())) {
                level.setState(VICTORY);
                break;
            }
        }
    }

    private void checkArenaTransition() {
        Arena currentArena = arenaController.getArena();

        if (currentArena.getState() == ArenaState.CLEARED) {
            arenaController = null;
            curArenaIDX++;
            activateNextArena();
        }
    }

    public void activateNextArena() {
        List<Arena> allArenas = level.getArenas();

        if (curArenaIDX < allArenas.size()) {
            Arena nextArena = allArenas.get(curArenaIDX);
            level.setCurArena(nextArena);
            arenaController = new ArenaController(nextArena, level);
        } else {
            spawnBoss();
        }
    }

    public void spawnChests() {
        List<Arena> arenas = level.getArenas();
        for (Arena a: arenas) {
            for (TilePosition p : a.getChestSpawnPoints()) {
                level.requestChestSpawn(p.x() * TILE_SIZE + TILE_SIZE / 2.0, p.y() * TILE_SIZE + TILE_SIZE / 2.0, Inventory.getRandomInventory());
            }
        }
    }

    public void spawnFences() {
        List<Arena> arenas = level.getArenas();
        for (Arena a: arenas) {
            for (TilePosition p : a.getFenceSpawnPoints()) {
                level.requestFenceSpawn(p.x() * TILE_SIZE + TILE_SIZE / 2.0, p.y() * TILE_SIZE + TILE_SIZE / 2.0);
            }
        }
    }

    public ArenaController getArenaController() {
        return arenaController;
    }

    public Level getLevel() {
        return level;
    }

}
