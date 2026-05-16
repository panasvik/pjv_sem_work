package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public enum PacketSubType implements Serializable {
    SPAWN_MOB,
    SPAWN_PROJECTILE,
    SPAWN_GOLD,
    SPAWN_GRATE,
    SPAWN_FENCE,
    SPAWN_CHEST,
    SPAWN_BOSS,
    SPAWN_HERO,
    UPDATE_HERO,
    UPDATE_MOB,
    UPDATE_PROJECTILE,
    REMOVE_ENTITY,
    REMOVE_PROJECTILE
}
