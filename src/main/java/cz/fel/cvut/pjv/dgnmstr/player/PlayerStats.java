package cz.fel.cvut.pjv.dgnmstr.player;

import java.io.Serializable;

public record PlayerStats(
        int damage,
        int defence,
        int hp,
        int range,
        int dexterity,
        int speed,
        int gold
) implements Serializable {
}
