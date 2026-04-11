package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record EntityRemovePacket(int id) implements Serializable {
}
