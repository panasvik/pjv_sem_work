package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.Mob;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private ArenaState state = ArenaState.NOT_STARTED;

    private final double startX;
    private final double startY;
    private final double width = 64.0;
    private final double height = 64.0;

    private final Level level;

    private final List<Mob> activeMobs = new ArrayList<>();
    private final List<Entity> grates = new ArrayList<>(); // Решетки на входах

    public Arena(double startX, double startY, Level level) {
        this.startX = startX;
        this.startY = startY;
        this.level = level;
    }

    public void update(List<Hero> players) {
        switch (state) {
            case NOT_STARTED -> checkPlayerEntry(players);
            case WAVE_FIGHT  -> checkWaveProgress();
            case MINI_BOSS_FIGHT  -> checkBossProgress();
            case CLEARED     -> {  }
        }
    }

    private void checkPlayerEntry(List<Hero> players) {
        Hero firstPlayerToEnter = null;

        for (Hero player : players) {
            if (player.isAlive() && isInsideArena(player.getX(), player.getY())) {
                firstPlayerToEnter = player;
                break;
            }
        }

        if (firstPlayerToEnter != null) {
            this.state = ArenaState.WAVE_FIGHT;
            closeGrates();
            teleportOtherPlayers(firstPlayerToEnter, players);
            spawnInitialMobs();
            System.out.println("Fight started");
        }
    }

    private void checkWaveProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty() && state == ArenaState.WAVE_FIGHT) {
            System.out.println("Spawning Miniboss");
            spawnMiniBoss();
            this.state = ArenaState.MINI_BOSS_FIGHT;
        }
    }

    private void checkBossProgress() {
        activeMobs.removeIf(mob -> !mob.isAlive());

        if (activeMobs.isEmpty()) {
            System.out.println("Arena finished");
            openGrates();
            this.state = ArenaState.CLEARED;
        }
    }


    private void closeGrates() {

        int corridorStartY = (int) (startY + (height / 2) - 3);
        int corridorEndY = (int) (startY + (height / 2) + 3);

        for (int y = corridorStartY; y <= corridorEndY; y++) {
            Grate leftGrate = new Grate(startX, y, level);
            Grate rightGrate = new Grate(startX + width - 1, y, level);
            grates.add(leftGrate);
            grates.add(rightGrate);
            level.addEntity(leftGrate);
            level.addEntity(rightGrate);
        }
    }

    private void openGrates() {
        for (Entity grate : grates) {
            grate.die();
        }
        grates.clear();
    }

    private void teleportOtherPlayers(Hero firstPlayer, List<Hero> players) {
        for (Hero player : players) {
            if (player != firstPlayer && player.isAlive()) {
                player.setPos((int) firstPlayer.getX() + 1, (int) firstPlayer.getY());
            }
        }
    }

    private void spawnInitialMobs() {
        //TODO
    }

    private void spawnMiniBoss() {
        //TODO
    }

    private boolean isInsideArena(double x, double y) {
        return x >= startX && x <= (startX + width) &&
                y >= startY && y <= (startY + height);
    }

    public ArenaState getState() {
        return state;
    }
}