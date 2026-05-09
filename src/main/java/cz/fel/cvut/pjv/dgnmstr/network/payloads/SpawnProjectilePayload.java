package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import cz.fel.cvut.pjv.dgnmstr.entity.projectile.ProjectileType;

import java.io.Serializable;

public record SpawnProjectilePayload(int id, ProjectileType type, int radius, double posX, double posY, int masterID, int damage, double deltaX, double deltaY, double velocity) implements Serializable {
}
