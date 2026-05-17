package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public enum PacketType implements Serializable {
    ARENA_STATE,
    LEVEL_STATE,
    SYNC_STATS,
    SPAWN_MOB,
    UPDATE_MOB,
    ENTITY_REMOVE,
    SPAWN_PROJECTILE,
    UPDATE_PROJECTILE,
    UPDATE_HERO,
    SPAWN_GOLD,
    REMOVE_GOLD,
    SPAWN_GRATE,
    SPAWN_FENCE,
    SPAWN_CHEST,
    REMOVE_GRATE,
    SPAWN_BOSS,
    SPAWN_HERO,
    PLAYER_PROFILE,
    CLIENT_ID,
    SEED,
    REMOVE_PROJECTILE,
    PROFILE_UPDATE,
    USE_POTION
}
