package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.entity.MobStats;

public record SpawnMobPacket(int id, MobStats stats, double PosX, double PosY) {
}
