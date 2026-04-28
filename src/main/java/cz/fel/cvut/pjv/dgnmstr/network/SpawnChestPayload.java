package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record SpawnChestPayload(int id, double x, double y) implements Serializable {
}
