package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public enum PacketSubType implements Serializable {
    MOB,
    PROJECTILE,
    GOLD,
    GRATE,
    FENCE,
    CHEST,
    BOSS,
    HERO,
    ENTITY,
}
