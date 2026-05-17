package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.view.GameController;
import cz.fel.cvut.pjv.dgnmstr.view.GameRenderer;
/**
 * Game manager.
 */
public class GameManager {
    private static GameManager instance;
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private GameController gameController;
    private ClientNetworkManager clientNetworkManager;
    private ClientManager clientManager;
    private ServerNetworkManager serverNetworkManager;
    private ServerManager serverManager;
    private GameRenderer gameRenderer;
    private InputHandler inputHandler;

    public GameManager() {
    }

    public ClientNetworkManager getClientNetworkManager() {
        return clientNetworkManager;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public ServerNetworkManager getServerNetworkManager() {
        return serverNetworkManager;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void setClientNetworkManager(ClientNetworkManager cnm) {
        this.clientNetworkManager = cnm;
    }

    public void setClientManager(ClientManager cm) {
        this.clientManager = cm;
    }

    public void setServerNetworkManager(ServerNetworkManager snm) {
        this.serverNetworkManager = snm;
    }

    public void setServerManager(ServerManager sm) {
        this.serverManager = sm;
    }

    public void setGameRenderer(GameRenderer gm) {
        this.gameRenderer = gm;
    }

    public void setInputHandler(InputHandler ih) {
        this.inputHandler = ih;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
