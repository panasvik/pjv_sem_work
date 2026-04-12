package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.ArenaState;

import java.io.Serializable;

public record ArenaStatePacket(ArenaState state) implements Serializable {
}
