package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;

import java.io.Serializable;

public record ArenaStatePayload(ArenaState state) implements Serializable {
}
