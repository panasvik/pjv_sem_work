package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;

import java.io.Serializable;

public record SpawnMobPayload(int id, MobStats stats, double PosX, double PosY, int mult) implements Serializable {
}
