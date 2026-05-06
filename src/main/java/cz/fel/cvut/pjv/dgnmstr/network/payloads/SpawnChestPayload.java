package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import cz.fel.cvut.pjv.dgnmstr.player.Inventory;

import java.io.Serializable;

public record SpawnChestPayload(int id, double x, double y, Inventory inventory) implements Serializable {
}
