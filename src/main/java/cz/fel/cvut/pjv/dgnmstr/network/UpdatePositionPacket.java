package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record UpdatePositionPacket(int id, double posX, double posY) implements Serializable {
}
