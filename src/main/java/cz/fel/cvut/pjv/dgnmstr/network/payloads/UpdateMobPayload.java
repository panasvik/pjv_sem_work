package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record UpdateMobPayload(int id,
                               double x,
                               double y,
                               int hp,
                               double lookX,
                               double lookY,
                               int attackCount,
                               long counter) implements Serializable, IDLinked, UpdateCounter {

    public UpdateMobPayload(int id, double x, double y, int hp, long counter) {
        this(id, x, y, hp, x, y, 0, counter);
    }
}
