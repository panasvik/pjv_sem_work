package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record SpawnFencePayload(int id,
                                double x,
                                double y) implements Serializable, IDLinked {
}
