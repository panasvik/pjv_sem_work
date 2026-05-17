package cz.fel.cvut.pjv.dgnmstr.entity.boss;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Faction;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;

import java.util.List;
import java.util.logging.Logger;
/**
 * Base class for bosses which are stronger than mobs and have extra abilities.
 */
public abstract class Boss extends Mob {
    protected List<Integer> phaseThresholds;
    private int currPhase = 0;
    private static final Logger log =  Logger.getLogger(Boss.class.getName());

    /**
     * Creates a new Boss.
     * @param id unique identifier
     * @param hp hit points value
     * @param speed movement speed
     * @param positionX initial X coordinate
     * @param positionY initial Y coordinate
     * @param halfHitBox half the collision box size
     * @param damage amount of damage
     * @param reach reach
     * @param gold amount of gold
     * @param faction side this belongs to
     * @param level the level this belongs to
     * @param phaseThresholds phase thresholds
     */
    public Boss(int id, int hp, int speed, double positionX, double positionY, double halfHitBox, int damage, int reach, int gold, Faction faction, Level level, List<Integer> phaseThresholds) {
        super(id, hp, speed, positionX, positionY, halfHitBox, damage, reach, gold, faction, level, 1);
        this.phaseThresholds = phaseThresholds;
    }



    @Override
    protected void onDamageTaken(int amount, Entity source) {
        if ((currPhase < this.phaseThresholds.size()) && (this.getHp() <= this.phaseThresholds.get(currPhase))) {
            currPhase += 1;
            log.info("Boss " + this.getClass().getSimpleName() +" entering phase " + currPhase);
            changePhase();
        }

    }

    protected abstract void changePhase();

    public int getCurrPhase() {
        return currPhase;
    }

    /**
     * Advances state by one game tick.
     * @param deltaTime delta time
     */
    public abstract void update(double deltaTime);

}
