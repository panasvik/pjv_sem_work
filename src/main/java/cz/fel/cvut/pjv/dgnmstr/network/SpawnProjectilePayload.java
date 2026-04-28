package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;

import java.io.Serializable;

public record SpawnProjectilePayload(int id, int radius, double posX, double posY, int masterID, int damage, double deltaX, double deltaY, double velocity) implements Serializable {
}
