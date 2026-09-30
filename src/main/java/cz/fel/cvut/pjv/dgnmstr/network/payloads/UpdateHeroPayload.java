package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record UpdateHeroPayload(int id,
                                double x,
                                double y,
                                int hp,
                                int gold,
                                double lookX,
                                double lookY,
                                int attackCount,
                                boolean ultimateActive,
                                long counter) implements Serializable, IDLinked, UpdateCounter {

    public UpdateHeroPayload(int id, double x, double y, int hp, int gold, long counter) {
        this(id, x, y, hp, gold, x, y, 0, false, counter);
    }
}
