package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.global.GameManager;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
        ServerNetworkManager serverNetworkManager = new ServerNetworkManager(port);
        gameManager.setSnm(serverNetworkManager);
        serverNetworkManager.start();
        gameManager.setSm(serverNetworkManager.getServerManager());
        socketToConnect.setText(serverIP + ":" + port);
        try {
            ClientNetworkManager clientNetworkManager = new ClientNetworkManager(serverIP, port, profile);
            gameManager.setCnm(clientNetworkManager);
            gameManager.setCm(clientNetworkManager.getManager());
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
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.activate(View.LEVEL);
        ServerNetworkManager serverNetworkManager = gameManager.getSnm();
        if (serverNetworkManager != null) {
            serverNetworkManager.sendIDs();
            serverNetworkManager.setGameStarted(true);
            serverNetworkManager.getServerManager().startLC();
        }
    }

    @FXML
    public void load() {
        gameManager = GameManager.getInstance();
        try {
            profile = PlayerProfile.loadFromJson();
        } catch (IOException e) {
            log.severe("Path does not exist");
            return;
        }
        isProfileLoaded = true;
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
            ClientNetworkManager clientNetworkManager = new ClientNetworkManager(host, port, profile);
            clientNetworkManager.connect();
            gameManager.setCnm(clientNetworkManager);
            gameManager.setCm(clientNetworkManager.getManager());
        } catch (UnknownHostException e) {
            log.severe("HOST DOES NOT EXIST");
            socketToConnect.setText("HOST DOES NOT EXIST");
            return;
        }
        isConnected = true;
    }
}
