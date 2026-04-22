package cz.fel.cvut.pjv.dgnmstr.dungeon.service;

public enum DifficultyStats {
    MEDIUM(28, 10, 2, 6, 56, 1.2);

    private final int mobCap;
    private final int initCap;
    private final int spawnRate;
    private final int timePeriod;
    private final int totalSpawns;
    private final double statsMult;

    DifficultyStats(int mobCap, int initCap, int spawnRate, int timePeriod, int totalSpawns, double statsMult) {
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

    public double getStatsMult() {
        return statsMult;
    }
}
