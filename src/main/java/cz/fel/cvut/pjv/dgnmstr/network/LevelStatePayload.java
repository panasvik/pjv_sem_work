package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;

import java.io.Serializable;

public record LevelStatePayload(LevelState state) implements Serializable {
}
