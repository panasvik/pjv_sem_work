package cz.fel.cvut.pjv.dgnmstr.dungeon;

import cz.fel.cvut.pjv.dgnmstr.entity.*;

public enum ArenaType {
    UNDEAD_CRYPT,
    GOBLIN_CAMP,
    SPIDER_NEST,
    FORGE,
    ROYAL_TOMB;

    public Mob spawnRandomMob(double x, double y, Level level) {
        double chance = Math.random();

        switch (this) {
            case UNDEAD_CRYPT:
                return chance > 0.3 ? new Zombie(x, y, level) : new Ghost(x, y, level);
            case ROYAL_TOMB:
                return new EvilWizard(x, y, level);
            // TODO: add other mobs to spawn
            default:
                return new Zombie(x, y, level);
        }
    }

    public Mob spawnMiniBoss(double x, double y, Level level) {
        double chance = Math.random();
        int miniBossMult = 3;
        switch (this) {
            case UNDEAD_CRYPT:
                return chance > 0.5 ? new Zombie(x, y, level, miniBossMult) : new Ghost(x, y, level, miniBossMult);
            case ROYAL_TOMB:
                return new EvilWizard(x, y, level, miniBossMult);
            default:
                return new Zombie(x, y, level, miniBossMult);
        }
    }
}