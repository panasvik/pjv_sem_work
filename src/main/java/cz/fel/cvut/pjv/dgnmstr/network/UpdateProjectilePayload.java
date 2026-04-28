package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record UpdateProjectilePayload(int id, int x, int y, boolean isAlive) implements Serializable {
}
