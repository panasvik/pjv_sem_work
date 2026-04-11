package cz.fel.cvut.pjv.dgnmstr.global;

public class IdGen {
    private static int id = 1;
    public static int getNextId() {
        return id++;
    }
}
