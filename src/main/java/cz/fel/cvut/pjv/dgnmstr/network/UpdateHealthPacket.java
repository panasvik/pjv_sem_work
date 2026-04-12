package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record UpdateHealthPacket(int id, int hp) implements Serializable {
}
