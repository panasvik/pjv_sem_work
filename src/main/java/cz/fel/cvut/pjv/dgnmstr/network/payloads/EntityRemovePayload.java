package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record EntityRemovePayload(int id) implements Serializable {
}
