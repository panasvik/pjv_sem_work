package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record DGNPacket(PacketType type, PacketSubType subType, Serializable payload) implements Serializable {
    public DGNPacket(PacketType type, Serializable payload) {
        this(type, null, payload);
    }
}
