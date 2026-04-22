package cz.fel.cvut.pjv.dgnmstr.dungeon.service;

import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;

public record MobSpawnRate(MobStats type, double chance) {

}
