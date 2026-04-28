package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;

import java.io.Serializable;

public record SyncPlayerStatsPayload(int id, PlayerStats stats) implements Serializable {
}
