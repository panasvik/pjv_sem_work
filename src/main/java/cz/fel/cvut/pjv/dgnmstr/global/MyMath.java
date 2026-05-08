package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.interfaces.Positionable;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


import static cz.fel.cvut.pjv.dgnmstr.entity.map.Chest.MAX_DIST_TO_OPEN;

public class MyMath {

    private static final Logger log = Logger.getLogger(MyMath.class.getName());

    public static double sigmoid(double x) {
        double midpoint = 100.0;
        double scale = 36.07;

        return 1 / (1 + Math.exp(-(x - midpoint) / scale));
    }

    public static <T extends Positionable> double calculateDistanceSquared(T a, T b) {
        double dx = a.getCenterX() - b.getCenterX();
        double dy = a.getCenterY() - b.getCenterY();
        return dx * dx + dy * dy;
    }

    public static Chest getClosestChest(List<Chest> chests, Hero player) {
        if (chests == null || chests.isEmpty()) {
            return null;
        }

        Chest closestChest = null;
        double minDistanceSquared = MAX_DIST_TO_OPEN;

        for (Chest chest : chests) {
            double distanceSquared = MyMath.calculateDistanceSquared(chest, player);

            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                closestChest = chest;
            }
        }

        return closestChest;
    }

    public static double calculateDistanceSquared(double dx, double dy) {
        return dx * dx + dy * dy;
    }

    public static void tryWait(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            log.severe("FATAL ERROR");
        }
    }
}
