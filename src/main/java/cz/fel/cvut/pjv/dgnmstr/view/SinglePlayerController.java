package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Role;
import cz.fel.cvut.pjv.dgnmstr.global.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

public class SinglePlayerController {
    @FXML
    public void newWarrior() {
        PlayerProfile newPlayer = new PlayerProfile(Role.WARRIOR);
        List<PlayerProfile> newPlayers = new ArrayList<>();
        newPlayers.add(newPlayer);
        ClientManager gameManager = ClientManager.getInstance();
        gameManager.setProfiles(newPlayers);
        gameManager.setThisProfile(newPlayer);
        toMainMenu();
    }

    /**
     * choose a name to save
     */
    @FXML
    public void ChooseName() {

    }


    @FXML
    public void loadCharacter() {

    }

    @FXML
    public void back() {
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.activate(View.START_MENU);
    }

    private void toMainMenu() {
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.activate(View.MAIN_MENU);
    }
}
