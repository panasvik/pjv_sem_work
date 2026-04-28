package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.global.GameEngine;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class ServerNetworkManager {
    private static final Logger log = Logger.getLogger(ServerNetworkManager.class.getName());

    private ServerSocket tcpServerSocket;
    private DatagramSocket udpSocket;

    private final List<ClientConnection> clients = new CopyOnWriteArrayList<>();
    private boolean isRunning = false;

    private final int tcpPort;
    private final int udpPort;

    public ServerNetworkManager(int tcpPort, int udpPort) {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public void start() {
        try {
            tcpServerSocket = new ServerSocket(tcpPort);
            udpSocket = new DatagramSocket(udpPort);
            isRunning = true;

            // Поток для приема новых TCP-соединений
            Thread acceptThread = new Thread(this::acceptClientsLoop, "TCP-Accept-Thread");
            acceptThread.start();

            // Поток для приема входящих UDP-пакетов
            Thread udpListenThread = new Thread(this::listenUdpLoop, "UDP-Listen-Thread");
            udpListenThread.start();

            log.info("ServerNetworkManager started. TCP: " + tcpPort + ", UDP: " + udpPort);
        } catch (IOException e) {
            log.severe("Failed to start server: " + e.getMessage());
        }
    }

    public void broadcast(DGNPacket packet) {
        if (!isRunning) return;

        if (shouldUseTCP(packet.type())) {
            broadcastTCP(packet);
        } else {
            broadcastUDP(packet);
        }
    }


    private boolean shouldUseTCP(PacketType type) {
        return switch (type) {
            case UPDATE_MOB, UPDATE_PROJECTILE, UPDATE_HERO -> false;
            default -> true;
        };
    }

    private void broadcastTCP(DGNPacket packet) {
        for (ClientConnection client : clients) {
            client.sendTCP(packet);
        }
    }

    private void broadcastUDP(DGNPacket packet) {
        try {
            byte[] data = SerializationUtils.serialize(packet);

            for (ClientConnection client : clients) {
                if (client.getUdpPort() != -1) { // Если мы знаем UDP-порт клиента
                    DatagramPacket udpPacket = new DatagramPacket(data, data.length, client.getAddress(), client.getUdpPort());
                    udpSocket.send(udpPacket);
                }
            }
        } catch (IOException e) {
            log.warning("Failed to broadcast UDP packet: " + e.getMessage());
        }
    }


    private void acceptClientsLoop() {
        while (isRunning) {
            try {
                Socket clientSocket = tcpServerSocket.accept();
                clientSocket.setTcpNoDelay(true); // Отключаем алгоритм Нейгла для игр

                ClientConnection connection = new ClientConnection(clientSocket);
                clients.add(connection);

                Thread clientThread = new Thread(() -> listenTcpClient(connection), "TCP-Client-" + connection.getId());
                clientThread.start();

                log.info("New client connected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                if (isRunning) log.severe("Error accepting client: " + e.getMessage());
            }
        }
    }

    private void listenTcpClient(ClientConnection client) {
        try {
            while (isRunning && client.isConnected()) {
                DGNPacket packet = (DGNPacket) client.getOis().readObject();
                handleIncomingPacket(packet, client);
            }
        } catch (EOFException | SocketException e) {
            log.info("Client disconnected: " + client.getId());
        } catch (Exception e) {
            log.warning("Error reading TCP from client: " + e.getMessage());
        } finally {
            disconnectClient(client);
        }
    }

    private void listenUdpLoop() {
        byte[] buffer = new byte[4096];
        while (isRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);

                DGNPacket dgnPacket = (DGNPacket) SerializationUtils.deserialize(packet.getData());

                updateClientUdpPort(packet.getAddress(), packet.getPort());

                handleIncomingPacket(dgnPacket, null);
            } catch (Exception e) {
                if (isRunning) log.warning("Error reading UDP: " + e.getMessage());
            }
        }
    }


    private void handleIncomingPacket(DGNPacket packet, ClientConnection source) {
        //GameEngine.getInstance().addIncomingPacket(packet);

        log.fine("Received packet: " + packet.type());
    }

    private void updateClientUdpPort(InetAddress address, int port) {
        for (ClientConnection c : clients) {
            if (c.getAddress().equals(address) && c.getUdpPort() == -1) {
                c.setUdpPort(port);
                log.info("Mapped UDP port " + port + " to client " + address);
            }
        }
    }

    private void disconnectClient(ClientConnection client) {
        clients.remove(client);
        client.close();
    }

    public void stop() {
        isRunning = false;
        try {
            if (tcpServerSocket != null) tcpServerSocket.close();
            if (udpSocket != null) udpSocket.close();
            for (ClientConnection c : clients) {
                c.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}