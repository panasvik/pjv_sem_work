package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record SpawnGoldPayload(int id,
                               double x,
                               double y,
                               int amount) implements Serializable, IDLinked {
}
