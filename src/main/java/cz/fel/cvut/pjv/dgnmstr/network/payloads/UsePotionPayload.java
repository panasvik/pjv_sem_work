package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record UsePotionPayload(int heroId, String potionType) implements Serializable {
}
