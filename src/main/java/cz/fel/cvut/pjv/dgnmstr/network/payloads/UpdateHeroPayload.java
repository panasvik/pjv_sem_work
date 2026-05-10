package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record UpdateHeroPayload(int id, double x, double y, int hp,
                                int gold) implements Serializable {
}
