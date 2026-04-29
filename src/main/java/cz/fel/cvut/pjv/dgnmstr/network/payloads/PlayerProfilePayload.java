package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.Serializable;

public record PlayerProfilePayload(PlayerProfile profile) implements Serializable {
}
