package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;

public record SyncPlayerStatsPacket(int id, PlayerStats stats) {
}
