package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.Serializable;

public record SpawnHeroPayload(int id, double x, double y, PlayerProfile profile) implements Serializable {
}
