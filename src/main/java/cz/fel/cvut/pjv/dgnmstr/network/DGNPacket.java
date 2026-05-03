package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record DGNPacket(PacketType type, Serializable payload) implements Serializable {
}
