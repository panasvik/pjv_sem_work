package cz.fel.cvut.pjv.dgnmstr.entity.mobs;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Zombie extends MeleeMob{
    private final Image sheet = new Image(getClass().getResourceAsStream("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Zombie.png"));

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
    public void update(double deltaTime) {

    }

    @Override
    public Image getImage() {
        return new WritableImage(sheet.getPixelReader(), 0, 0, 32, 32);
    }
}
