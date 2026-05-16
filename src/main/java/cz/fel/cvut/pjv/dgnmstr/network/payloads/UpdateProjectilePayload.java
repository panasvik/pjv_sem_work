package cz.fel.cvut.pjv.dgnmstr.network.payloads;

import java.io.Serializable;

public record UpdateProjectilePayload(int id,
                                      double x,
                                      double y,
                                      boolean isAlive,
                                      long counter) implements Serializable, IDLinked, UpdateCounter {
}
