package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record SpawnProjectilePayload(int id, int radius, double posX, double posY, int masterID, int damage, double deltaX, double deltaY, double velocity) implements Serializable {
}
