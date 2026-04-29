package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record LevelSeedPayload(long seed) implements Serializable {
}
