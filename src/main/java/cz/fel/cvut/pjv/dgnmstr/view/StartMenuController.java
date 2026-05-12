package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.global.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.global.MyMath;
import cz.fel.cvut.pjv.dgnmstr.global.ServerManager;
import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class StartMenuController {
    private static final Logger log = Logger.getLogger(StartMenuController.class.getName());

    private GameManager gameManager;

    private boolean isConnected = false;

    @FXML
    public Label socketToConnect;

    @FXML
    public TextField hostIP;

    @FXML
    public TextField hostPort;

    boolean isProfileLoaded = false;

    private PlayerProfile profile;
    @FXML
    public TextField filePath;

    @FXML
    public Button heroViewButton;

    @FXML
    public void host() {
        if (!isProfileLoaded) {
            log.warning("Profile is not loaded");
            socketToConnect.setText("Load profile first");
            return;
        }

        int port;

        if (hostPort.getText().isEmpty()) {
            port = 5000;
        } else {
            port = Integer.parseInt(hostPort.getText());

        }
        String serverIP;
        if (hostIP.getText().isEmpty()) {
            serverIP = "127.0.0.1";
        } else {
            serverIP = hostIP.getText();
        }
        ServerManager serverManager = new ServerManager(port);
        gameManager.setServerNetworkManager(serverManager.getSnm());
        serverManager.getSnm().start();
        gameManager.setServerManager(serverManager);
        socketToConnect.setText(serverIP + ":" + port);
        try {
            ClientManager clientManager = new ClientManager(serverIP, port, profile);
            gameManager.setClientManager(clientManager);
            ClientNetworkManager clientNetworkManager = clientManager.getNetworkManager();
            gameManager.setClientNetworkManager(clientNetworkManager);
            clientNetworkManager.connect();
        } catch (UnknownHostException | NumberFormatException k) {
            log.severe("HOST DOES NOT EXIST");
            socketToConnect.setText("HOST DOES NOT EXIST");
            return;
        }
        isConnected = true;
    }

    @FXML
    public void start() {
        if(!isConnected) {
            log.warning("server is not started. start it first");
            return;
        }
        ServerNetworkManager serverNetworkManager = gameManager.getServerNetworkManager();
        if (serverNetworkManager != null) {
            ServerManager serverManager = gameManager.getServerManager();
            serverManager.generateNewLevel();
            MyMath.tryWait(200);
            serverNetworkManager.sendSeeds();
            MyMath.tryWait(200);
            serverNetworkManager.sendIDs();
            serverNetworkManager.setGameStarted(true);
            serverManager.startLC();
        }
        ClientManager clientManager = gameManager.getClientManager();
        if (clientManager.isReady()) {
            SceneManager sceneManager = SceneManager.getInstance();
            sceneManager.activate(View.LEVEL);
        }
    }

    @FXML
    public void initialize() {
        gameManager = GameManager.getInstance();
    }


    @FXML
    public void load() {
        profile = PlayerProfile.loadFromJson();
        isProfileLoaded = true;
        heroViewButton.setDisable(false);
    }

    @FXML
    public void loadFrom() {
        gameManager = GameManager.getInstance();
        try {
            profile = PlayerProfile.loadFromJson(filePath.getText());
        } catch (IOException e) {
            log.severe("Path does not exist");
            return;
        }
        isProfileLoaded = true;
        heroViewButton.setDisable(false);
    }

    @FXML
    public void saveProfile() {
        if (!isProfileLoaded) {
            log.warning("Profile is not loaded");
            return;

        }
        try {
            profile.saveToJson(filePath.getText());
        } catch (IOException e) {
            log.severe("UNABLE TO SAVE PROFILE");
        }
    }

    @FXML
    public void exit() {
        Platform.exit();
        ClientNetworkManager clientNetworkManager = gameManager.getClientNetworkManager();
        if (clientNetworkManager != null) {
            clientNetworkManager.disconnect();
        }
        ClientManager clientManager = gameManager.getClientManager();
        if (clientManager != null) {
            clientManager.stopManagingInputs();
        }
        ServerNetworkManager serverNetworkManager = gameManager.getServerNetworkManager();
        if (serverNetworkManager != null) {
            serverNetworkManager.stop();
        }
        ServerManager serverManager = gameManager.getServerManager();
        if (serverManager != null) {
            serverManager.stop();
        }
    }

    @FXML
    public void connect() {
        if (!isProfileLoaded) {
            log.warning("Profile is not loaded");
            socketToConnect.setText("Load profile first");
            return;
        }

        String host = hostIP.getText();
        String portText = hostPort.getText();
        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            log.warning("Port is not a valid number");
            socketToConnect.setText("Invalid port");
            return;
        }
        try {
            ClientManager clientManager = new ClientManager(host, port, profile);
            ClientNetworkManager clientNetworkManager = clientManager.getNetworkManager();
            clientNetworkManager.connect();
            gameManager.setClientManager(clientManager);
            gameManager.setClientNetworkManager(clientNetworkManager);
        } catch (UnknownHostException e) {
            log.severe("HOST DOES NOT EXIST");
            socketToConnect.setText("HOST DOES NOT EXIST");
            return;
        }
        isConnected = true;
    }

    @FXML
    public void openHeroView(ActionEvent actionEvent) {
        HeroView heroView = new HeroView();
        Parent root = heroView.build(profile);
        SceneManager.getInstance().registerScreen(View.HERO, root);
        SceneManager.getInstance().activate(View.HERO);
        log.info("Hero view opened");
    }
}
