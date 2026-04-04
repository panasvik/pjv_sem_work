package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;

public class DroppedGold extends Entity {
    private int value;
    private static final double PICKUP_RADIUS = Level.TILE_SIZE / 2.0;
    public DroppedGold(double positionX, double positionY, Level level, int value) {
        super(1, 0, positionX, positionY, Faction.NEUTRAL, level);
        this.value = value;
    }


    @Override
    public void update() {
        if (!isAlive()) return;

        for (Hero player : getLevel().getPlayers()) {
            if (player.isAlive()) {
                double distSquared = MyMath.calculateDistanceSquared(this, player);

                if (distSquared <= (PICKUP_RADIUS * PICKUP_RADIUS)) {
                    player.addGold(value);
                    System.out.println("Подобрано золота: " + value + ". Всего: " + player.getGold());

                    this.die();
                    break;
                }
            }
        }
    }

    @Override
    protected void onDamageTaken(int amount, Entity source) {

    }

    @Override
    protected int applyDefence(int damage) {
        return 0;
    }

    public int getValue() {
        return value;
    }
}
