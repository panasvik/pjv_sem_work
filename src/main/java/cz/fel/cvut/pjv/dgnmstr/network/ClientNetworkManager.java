package cz.fel.cvut.pjv.dgnmstr.network;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class ClientNetworkManager {
    private static final Logger log = Logger.getLogger(ClientNetworkManager.class.getName());

    // TCP
    private Socket tcpSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    // UDP
    private DatagramSocket udpSocket;

    private final InetAddress serverAddress;
    private final int serverTcpPort;
    private final int serverUdpPort;

    private boolean isRunning = false;

    public ClientNetworkManager(String serverIp, int serverTcpPort, int serverUdpPort) throws UnknownHostException {
        this.serverAddress = InetAddress.getByName(serverIp);
        this.serverTcpPort = serverTcpPort;
        this.serverUdpPort = serverUdpPort;
    }


    public void connect() {
        try {
            tcpSocket = new Socket(serverAddress, serverTcpPort);
            tcpSocket.setTcpNoDelay(true);

            oos = new ObjectOutputStream(tcpSocket.getOutputStream());
            ois = new ObjectInputStream(tcpSocket.getInputStream());

            udpSocket = new DatagramSocket();

            isRunning = true;

            Thread tcpThread = new Thread(this::listenTcpLoop, "Client-TCP-Listen");
            tcpThread.setDaemon(true);
            tcpThread.start();

            Thread udpThread = new Thread(this::listenUdpLoop, "Client-UDP-Listen");
            udpThread.setDaemon(true);
            udpThread.start();

            log.info("Connected to server! TCP Local Port: " + tcpSocket.getLocalPort() + ", UDP Local Port: " + udpSocket.getLocalPort());

            sendUdpHandshake();

        } catch (IOException e) {
            log.severe("Failed to connect to server: " + e.getMessage());
        }
    }


    public void send(DGNPacket packet) {
        if (!isRunning) return;

        if (shouldUseTCP(packet.type())) {
            sendTCP(packet);
        } else {
            sendUDP(packet);
        }
    }


    private boolean shouldUseTCP(PacketType type) {
        return switch (type) {
            case UPDATE_MOB, UPDATE_PROJECTILE, UPDATE_HERO -> false;
            default -> true;
        };
    }

    private void sendTCP(DGNPacket packet) {
        try {
            synchronized (oos) {
                oos.writeObject(packet);
                oos.flush();
                oos.reset();
            }
        } catch (IOException e) {
            log.warning("Failed to send TCP packet: " + e.getMessage());
        }
    }

    private void sendUDP(DGNPacket packet) {
        try {
            byte[] data = SerializationUtils.serialize(packet);
            DatagramPacket udpPacket = new DatagramPacket(data, data.length, serverAddress, serverUdpPort);
            udpSocket.send(udpPacket);
        } catch (IOException e) {
            log.warning("Failed to send UDP packet: " + e.getMessage());
        }
    }


    private void sendUdpHandshake() {
        sendUDP(new DGNPacket(PacketType.UPDATE_HERO, null));
    }



    private void listenTcpLoop() {
        try {
            while (isRunning && !tcpSocket.isClosed()) {
                DGNPacket packet = (DGNPacket) ois.readObject();
                handleIncomingPacket(packet);
            }
        } catch (EOFException | SocketException e) {
            log.info("Disconnected from server.");
            disconnect();
        } catch (Exception e) {
            log.warning("Error reading TCP stream: " + e.getMessage());
        }
    }

    private void listenUdpLoop() {
        byte[] buffer = new byte[4096];
        while (isRunning && !udpSocket.isClosed()) {
            try {
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(datagram);

                DGNPacket packet = (DGNPacket) SerializationUtils.deserialize(datagram.getData());
                handleIncomingPacket(packet);
            } catch (Exception e) {
                if (isRunning) log.warning("Error reading UDP stream: " + e.getMessage());
            }
        }
    }


    private void handleIncomingPacket(DGNPacket packet) {
        // ClientManager.getInstance().receiveNetworkEvent(packet);

        log.fine("Client received: " + packet.type());
    }

    public void disconnect() {
        isRunning = false;
        try {
            if (oos != null) oos.close();
            if (ois != null) ois.close();
            if (tcpSocket != null) tcpSocket.close();
            if (udpSocket != null) udpSocket.close();
        } catch (IOException e) {
            log.warning("Error during disconnect: " + e.getMessage());
        }
    }
}