package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.DifficultyStats;
import cz.fel.cvut.pjv.dgnmstr.dungeon.DungeonGenerator;
import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.LevelController;
import cz.fel.cvut.pjv.dgnmstr.entity.WarriorController;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import cz.fel.cvut.pjv.dgnmstr.view.GameRenderer;

import java.util.ArrayList;
import java.util.List;

import static cz.fel.cvut.pjv.dgnmstr.global.Difficulty.MID;

public class GameManager {
    private static GameManager instance = new GameManager();
    private Level level;
    private List<PlayerProfile> profiles;
    private PlayerProfile thisProfile;
    private final DungeonGenerator generator;
    private DifficultyStats currentDifficulty;
    private GameState state;
    private final GameEngine engine;
    private LevelController lc;
    private WarriorController wc;


    public GameManager() {
        this.profiles = new ArrayList<PlayerProfile>();
        this.generator = new DungeonGenerator();
        this.state = GameState.MAIN_MENU;
        this.engine = GameEngine.getInstance();
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
    public void newLevel() {
        level = generator.generate(chooseDifficulty(MID));
    }

    /**
     * loads to generated level,
     * adds Heroes as entities
     */
    public void loadPlayersToLevel() {
        for (PlayerProfile profile: profiles) {
            level.addPlayer(profile);
            GameManager gameManager = GameManager.getInstance();

            if (profile == gameManager.getThisProfile()) {
                WarriorController wc = new WarriorController(, scene);
                gameManager.setWc(wc);
            }
        }
    }


    public void startLevel() {
        lc = new LevelController(level);
        engine.startLC(lc);
        state = GameState.PLAYING;
    }

    /**
     * throws players back to lobby
     */
    public void throwToLobby() {
        engine.exitLevel();
        mergeLoot();
        state = GameState.MAIN_MENU;
    }

    /**
     * pauses tick timer
     */
    public void pauseGame() {
        engine.pauseEngine();
        state = GameState.PAUSE;
    }


    /**
     * resumes tick timer
     */
    public void resumeGame() {
        engine.unpauseEngine();
        state = GameState.PLAYING;
    }

    public void toHeroMenu() {
        state = GameState.HERO_MANAGER;
    }

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
    private void mergeLoot() {
        List<Hero> players = level.getPlayers();
        for (Hero hero: players) {
            hero.getProfile().setGold(hero.getGold());
        }
    }


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


    public PlayerProfile getThisProfile() {
        return thisProfile;
    }

    public DifficultyStats getCurrentDifficulty() {
        return currentDifficulty;
    }

    public GameState getState() {
        return state;
    }

    public void setThisProfile(PlayerProfile thisProfile) {
        this.thisProfile = thisProfile;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public WarriorController getWc() {
        return wc;
    }

    public void setWc(WarriorController wc) {
        this.wc = wc;
    }
}