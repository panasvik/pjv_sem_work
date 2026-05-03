package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Goblin extends MeleeMob {
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
    public ImgId getImgId() {
        return ImgId.GOBLIN;
    }
}


