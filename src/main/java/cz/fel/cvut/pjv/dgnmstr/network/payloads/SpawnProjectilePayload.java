package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import cz.fel.cvut.pjv.dgnmstr.global.ProjectileType;

import java.io.Serializable;

public record SpawnProjectilePayload(int id, ProjectileType type, int radius, double posX, double posY, int masterID, int damage, double deltaX, double deltaY, double velocity) implements Serializable {
}
