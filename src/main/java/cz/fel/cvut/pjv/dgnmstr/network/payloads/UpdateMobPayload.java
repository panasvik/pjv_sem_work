package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record UpdateMobPayload(int id, double x, double y, int hp)  implements Serializable {
}
