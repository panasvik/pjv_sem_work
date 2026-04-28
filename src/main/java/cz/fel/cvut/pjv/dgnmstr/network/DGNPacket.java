package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record DGNPacket(PacketType type, Object payload) implements Serializable {
}
