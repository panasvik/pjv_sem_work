package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.dungeon.DifficultyStats;
import cz.fel.cvut.pjv.dgnmstr.dungeon.DungeonGenerator;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static GameManager instance;
    private Level level;
    private List<PlayerProfile> profiles;
    private DungeonGenerator generator;
    private DifficultyStats currentDifficulty;
    private GameState state;

    public GameManager() {
        this.profiles = new ArrayList<PlayerProfile>();
        this.generator = new DungeonGenerator();
        this.state = GameState.MAIN_MENU;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * method to choose difficulty
     * @param dif difficulty chosen by player
     * @return DifficultyStats for Level
     */
    public DifficultyStats chooseDifficulty(Difficulty dif) {
        return DifficultyStats.MEDIUM;
    }

    /**
     * generates new Level
     */
    public void newLevel() {}

    /**
     * loads to generated level:
     * Heroes as entities
     * chests as entities
     * sets up arenas
     */
    public void loadNextLevel() {}

    /**
     * starts timer of the level
     */
    public void startLevel() {}

    /**
     * updates the game every tick,
     * calls update in Level, which calls update for
     * every entity and every Arena
     */
    public void update() {}

    /**
     * throws players back to lobby
     */
    public void throwToLobby() {}

    /**
     * pauses tick timer
     */
    public void pauseGame() {}


    /**
     * resumes tick timer
     */
    public void resumeGame() {}


    /**
     * loads all player profiles from the disc
     */
    public void loadProfiles() {}

    /**
     * saves all player profiles to the disc
     */
    public void saveProfiles() {}

    /**
     * syncs every player's PlayerProfile with Hero
     */
    private void mergeLoot() {}


    public Level getLevel() {
        return level;
    }
    public void setLevel(Level level) {
        this.level = level;
    }

    public List<PlayerProfile> getProfiles() {

        return profiles;
    }
    public void setProfiles(List<PlayerProfile> profiles) {
        this.profiles = profiles;
    }

    public void setGenerator(DungeonGenerator generator) {
        this.generator = generator;
    }

}