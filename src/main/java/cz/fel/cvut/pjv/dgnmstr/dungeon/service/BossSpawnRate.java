package cz.fel.cvut.pjv.dgnmstr.dungeon.service;

import cz.fel.cvut.pjv.dgnmstr.entity.boss.BossStats;

public record BossSpawnRate(BossStats type, double chance) {

}
