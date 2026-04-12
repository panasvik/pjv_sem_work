package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.LevelState;

import java.io.Serializable;

public record LevelStatePacket(LevelState state) implements Serializable {
}
