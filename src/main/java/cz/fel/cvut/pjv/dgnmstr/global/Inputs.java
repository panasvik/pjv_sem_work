package cz.fel.cvut.pjv.dgnmstr.global;

import java.io.Serializable;
/**
 * Immutable data carrier for inputs.
 */
public record Inputs(boolean ult,
                     boolean interact,
                     boolean attack,
                     boolean up,
                     boolean down,
                     boolean left,
                     boolean right,
                     double mouseX,
                     double mouseY) implements Serializable {
}
