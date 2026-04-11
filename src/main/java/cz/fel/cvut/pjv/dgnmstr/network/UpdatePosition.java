package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record UpdatePosition(int id, double posX, double posY) implements Serializable {
}
