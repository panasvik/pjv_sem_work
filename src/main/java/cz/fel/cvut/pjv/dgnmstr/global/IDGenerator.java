package cz.fel.cvut.pjv.dgnmstr.global;

import java.util.concurrent.atomic.AtomicInteger;
/**
 * Id generator.
 */
public class IDGenerator {
    private final AtomicInteger currentId;

    public IDGenerator() {
        this.currentId = new AtomicInteger(1); // Начинаем с 1 (0 можно оставить для системных нужд)
    }

    public int getNextId() {
        return currentId.getAndIncrement();
    }
}