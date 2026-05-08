package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.global.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class ClientNetworkManager {
    private static final Logger log = Logger.getLogger(ClientNetworkManager.class.getName());

    private DatagramSocket udpSocket;
    private final ClientManager manager;
    private final InetAddress serverAddress;
    private final int serverUdpPort;
    private boolean isRunning = false;

    public ClientNetworkManager(String serverIp, int serverUdpPort, PlayerProfile profile) throws UnknownHostException {
        this.serverAddress = InetAddress.getByName(serverIp);
        this.serverUdpPort = serverUdpPort;
        manager = new ClientManager(this, profile);
    }

    public void connect() {
        try {
            udpSocket = new DatagramSocket();
            isRunning = true;

            Thread udpThread = new Thread(this::listenUdpLoop, "Client-Network-Thread");
            udpThread.setDaemon(true);
            udpThread.start();

            System.out.println("Started UDP client! Local Port: " + udpSocket.getLocalPort());

            sendUdpHandshake();

        } catch (IOException e) {
            System.out.println("Failed to initialize UDP socket: " + e.getMessage());
        }
    }


    public void sendInputs(Inputs inputs) {
        if (!isRunning || inputs == null) return;
        sendUDP(new DGNPacket(PacketType.UPDATE_HERO, inputs));
    }

    private void sendUDP(DGNPacket packet) {
        try {
            byte[] data = SerializationUtils.serialize(packet);
            DatagramPacket udpPacket = new DatagramPacket(data, data.length, serverAddress, serverUdpPort);
            udpSocket.send(udpPacket);
        } catch (IOException e) {
            System.out.println("Failed to send UDP packet: " + e.getMessage());
        }
    }

    private void sendUdpHandshake() {
        PlayerProfilePayload payload = new PlayerProfilePayload(manager.getMyProfile());
        sendUDP(new DGNPacket(PacketType.PLAYER_PROFILE, payload));
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
        switch (packet.type()) {
            case SEED -> manager.makeNewLC(((LevelSeedPayload) packet.payload()).seed());
            case CLIENT_ID -> manager.setLocalHero(((ClientID) packet.payload()).id());
            case SPAWN_HERO -> manager.addPlayer((SpawnHeroPayload) packet.payload());
            case SPAWN_MOB -> manager.spawnMob((SpawnMobPayload) packet.payload());
            case SPAWN_BOSS -> manager.spawnBoss((SpawnBossPayload) packet.payload());
            case SPAWN_PROJECTILE -> manager.spawnProjectile((SpawnProjectilePayload) packet.payload());
            case SPAWN_GOLD -> manager.spawnGold((SpawnGoldPayload) packet.payload());
            case SPAWN_GRATE -> manager.spawnGrate((SpawnGratePayload) packet.payload());
            case SPAWN_CHEST -> manager.spawnChest((SpawnChestPayload) packet.payload());
            case SPAWN_FENCE -> manager.spawnFence((SpawnFencePayload) packet.payload());
            case UPDATE_HERO -> manager.updateHero((UpdateHeroPayload) packet.payload());
            case UPDATE_MOB -> manager.updateMob((UpdateMobPayload) packet.payload());
            case UPDATE_PROJECTILE -> manager.updateProjectile((UpdateProjectilePayload) packet.payload());
            case ENTITY_REMOVE -> manager.removeEntity((EntityRemovePayload) packet.payload());
            case LEVEL_STATE -> manager.updateLevelState((LevelStatePayload) packet.payload());
            case ARENA_STATE -> manager.updateArenaState((ArenaStatePayload) packet.payload());
            case SYNC_STATS -> manager.syncStats((SyncPlayerStatsPayload) packet.payload());
            case REMOVE_PROJECTILE -> manager.removeProjectile(((EntityRemovePayload) packet.payload()));
            default -> {
            }
        }
    }

    public void disconnect() {
        isRunning = false;
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
        System.out.println("Disconnected from server.");
    }

    public void sendProfileUpdate(PlayerProfile profile) {
        PlayerProfilePayload payload = new PlayerProfilePayload(profile);
        DGNPacket packet = new DGNPacket(PacketType.PROFILE_UPDATE, payload);
        sendUDP(packet);
        System.out.println("sent profile with " + profile.getGold() + " gold in it");
    }

    public ClientManager getManager() {
        return manager;
    }
}