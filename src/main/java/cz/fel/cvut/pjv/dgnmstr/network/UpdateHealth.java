package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.Serializable;

public record UpdateHealth(int id, int hp) implements Serializable {
}
