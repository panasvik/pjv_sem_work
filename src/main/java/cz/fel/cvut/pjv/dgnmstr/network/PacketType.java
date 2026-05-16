package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public enum PacketType implements Serializable {
    ARENA_STATE,
    LEVEL_STATE,
    SYNC_STATS,
    SPAWN,
    UPDATE,
    REMOVE,
    PLAYER_PROFILE,
    CLIENT_ID,
    SEED,
    PROFILE_UPDATE;
}
