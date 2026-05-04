package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.global.ImgId;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Zombie extends MeleeMob{
    public Zombie(int id,
                  double positionX, double positionY, Level level, int multiplicator) {
        super(id,
                MobStats.ZOMBIE.getHp(level.getDifficulty().getStatsMult()),
              MobStats.ZOMBIE.getSpeed(),
              positionX,
              positionY,
              MobStats.ZOMBIE.getDamage(level.getDifficulty().getStatsMult()),
              MobStats.ZOMBIE.getReach(),
              MobStats.ZOMBIE.getGold(),
              Faction.MONSTER,
              level, multiplicator);
    }
    public Zombie(int id,
                  double positionX, double positionY, Level level) {
        super(id,
                MobStats.ZOMBIE.getHp(level.getDifficulty().getStatsMult()),
                MobStats.ZOMBIE.getSpeed(),
                positionX,
                positionY,
                MobStats.ZOMBIE.getDamage(level.getDifficulty().getStatsMult()),
                MobStats.ZOMBIE.getReach(),
                MobStats.ZOMBIE.getGold(),
                Faction.MONSTER,
                level, 1);
    }



    @Override
    public ImgId getImgId() {
        return ImgId.ZOMBIE;
    }
}
