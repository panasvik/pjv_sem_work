package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Goblin extends MeleeMob {
    private final Image sheet = new Image(getClass().getResourceAsStream("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinScout-Sheet.png"));
    public Goblin(int id, double positionX, double positionY, Level level, int multiplicator) {
        super( id,
                MobStats.GOBLIN.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getSpeed(),
                positionX,
                positionY,
                MobStats.GOBLIN.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getReach(),
                MobStats.GOBLIN.getGold(),
                Faction.MONSTER,
                level, multiplicator);
    }
    public Goblin(int id, double positionX, double positionY, Level level) {
        super( id,
                MobStats.GOBLIN.getHp(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getSpeed(),
                positionX,
                positionY,
                MobStats.GOBLIN.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.GOBLIN.getReach(),
                MobStats.GOBLIN.getGold(),
                Faction.MONSTER,
                level, 1);
    }

    @Override
    public Image getImage() {
        return new WritableImage(sheet.getPixelReader(), 0, 0, 512, 512);
    }
}


