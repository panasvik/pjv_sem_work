package cz.fel.cvut.pjv.dgnmstr.dungeon.service;

public enum DifficultyStats {
    MEDIUM(10, 8, 2, 4, 20, 2);

    private final int mobCap;
    private final int initCap;
    private final int spawnRate;
    private final int timePeriod;
    private final int totalSpawns;
    private final int statsMult;
    /**
     * Enumeration of difficulty stats.
     */
    DifficultyStats(int mobCap, int initCap, int spawnRate, int timePeriod, int totalSpawns, int statsMult) {
        this.mobCap = mobCap;
        this.initCap = initCap;
        this.spawnRate = spawnRate;
        this.timePeriod = timePeriod;
        this.totalSpawns = totalSpawns;
        this.statsMult = statsMult;
    }

    public int getMobCap() {
        return mobCap;
    }

    public int getInitCap() {
        return initCap;
    }

    public int getSpawnRate() {
        return spawnRate;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public int getTotalSpawns() {
        return totalSpawns;
    }

    public int getStatsMult() {
        return statsMult;
    }
}
