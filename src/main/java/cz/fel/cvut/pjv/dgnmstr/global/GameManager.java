package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.view.GameController;
import cz.fel.cvut.pjv.dgnmstr.view.GameRenderer;

public class GameManager {
    private static GameManager instance;
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private GameController gameController;
    private ClientNetworkManager cnm;
    private ClientManager cm;
    private ServerNetworkManager snm;
    private ServerManager sm;
    private GameRenderer gm;
    private InputHandler ih;

    public GameManager() {
    }

    public ClientNetworkManager getCnm() {
        return cnm;
    }

    public ClientManager getCm() {
        return cm;
    }

    public ServerNetworkManager getSnm() {
        return snm;
    }

    public ServerManager getSm() {
        return sm;
    }

    public GameRenderer getGm() {
        return gm;
    }

    public InputHandler getIh() {
        return ih;
    }

    public void setCnm(ClientNetworkManager cnm) {
        this.cnm = cnm;
    }

    public void setCm(ClientManager cm) {
        this.cm = cm;
    }

    public void setSnm(ServerNetworkManager snm) {
        this.snm = snm;
    }

    public void setSm(ServerManager sm) {
        this.sm = sm;
    }

    public void setGm(GameRenderer gm) {
        this.gm = gm;
    }

    public void setIh(InputHandler ih) {
        this.ih = ih;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
