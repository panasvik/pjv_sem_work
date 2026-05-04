package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record UpdateHeroPayload(int id, double x, double y, int hp,
                                double mouseX, double mouseY, boolean isAttack,
                                boolean isUlt, boolean isInteract, int gold) implements Serializable {
}
