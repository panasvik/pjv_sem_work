package cz.fel.cvut.pjv.dgnmstr.view;

import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.network.GameManager;
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

    private ServerNetworkManager snm;
    private boolean isConnected = false;

    private ClientNetworkManager cnm;
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
        int port = Integer.parseInt(hostPort.getText());
        String serverIP = hostIP.getText();
        snm = new ServerNetworkManager(port);
        GameManager gameManager = GameManager.getInstance();
        gameManager.setSnm(snm);
        snm.start();
        gameManager.setSm(snm.getServerManager());
        socketToConnect.setText(String.valueOf(port));
        try {
            cnm = new ClientNetworkManager(serverIP, port, profile);
            gameManager.setCnm(cnm);
            gameManager.setCm(cnm.getManager());
            cnm.connect();
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
        if (snm != null) {
            snm.sendIDs();
            snm.setGameStarted(true);
            snm.getServerManager().startLC();
        }
    }

    @FXML
    public void load() {
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
        GameManager gameManager = GameManager.getInstance();
        try {
            cnm = new ClientNetworkManager(host, port, profile);
            cnm.connect();
            gameManager.setCnm(cnm);
            gameManager.setCm(cnm.getManager());
        } catch (UnknownHostException e) {
            log.severe("HOST DOES NOT EXIST");
            socketToConnect.setText("HOST DOES NOT EXIST");
            return;
        }
        isConnected = true;
    }
}
