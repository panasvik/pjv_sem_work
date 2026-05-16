package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;
import cz.fel.cvut.pjv.dgnmstr.global.ClientManager;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ClientNetworkManager {
    private static final Logger log = Logger.getLogger(ClientNetworkManager.class.getName());
    private static final int BUFFER_SIZE = 4096;

    private DatagramSocket udpSocket;
    private final ClientManager manager;
    private final InetAddress serverAddress;
    private final int serverUdpPort;
    private boolean isRunning = false;

    private final Map<Integer, Long> updateCounters = new ConcurrentHashMap<>();


    public ClientNetworkManager(String serverIp, int serverUdpPort, ClientManager manager) throws UnknownHostException {
        this.serverAddress = InetAddress.getByName(serverIp);
        this.serverUdpPort = serverUdpPort;
        this.manager = manager;
    }

    public void connect() {
        try {
            udpSocket = new DatagramSocket();
            isRunning = true;

            Thread udpThread = new Thread(this::listenUdpLoop);
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

        sendUDP(new DGNPacket(PacketType.UPDATE, PacketSubType.UPDATE_HERO, inputs));
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
        byte[] buffer = new byte[BUFFER_SIZE];
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
        PacketType type = packet.type();
        switch (type) {
            case SEED -> manager.makeNewLC(((LevelSeedPayload) packet.payload()).seed());
            case CLIENT_ID -> manager.setLocalHero(((ClientID) packet.payload()).id());
            case SPAWN -> manageNewSpawn(packet.subType(), packet);
            case UPDATE -> manageUpdate(packet.subType(), packet);
            case LEVEL_STATE -> manageNewLevelState(packet);
            case ARENA_STATE -> manager.updateArenaState((ArenaStatePayload) packet.payload());
            case SYNC_STATS -> manager.syncStats((SyncPlayerStatsPayload) packet.payload());
            case REMOVE -> manageRemove(packet.subType(), packet);
            default -> { }
        }
    }

    public void disconnect() {
        isRunning = false;
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
        log.info("disconnected from the server " + serverAddress + ":" + serverUdpPort);
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

    private void manageNewLevelState(DGNPacket packet) {
        LevelStatePayload payload = (LevelStatePayload) packet.payload();
        manager.updateLevelState(payload);
        LevelState state = payload.state();
        if (state == LevelState.GAME_OVER || state == LevelState.VICTORY) {
            updateCounters.clear();
        }
    }

    private void manageNewSpawn(PacketSubType subtype, DGNPacket packet) {
        switch (subtype) {
            case SPAWN_HERO -> manager.addPlayer((SpawnHeroPayload) packet.payload());
            case SPAWN_MOB -> manager.spawnMob((SpawnMobPayload) packet.payload());
            case SPAWN_BOSS -> manager.spawnBoss((SpawnBossPayload) packet.payload());
            case SPAWN_PROJECTILE -> manager.spawnProjectile((SpawnProjectilePayload) packet.payload());
            case SPAWN_GOLD -> manager.spawnGold((SpawnGoldPayload) packet.payload());
            case SPAWN_GRATE -> manager.spawnGrate((SpawnGratePayload) packet.payload());
            case SPAWN_CHEST -> manager.spawnChest((SpawnChestPayload) packet.payload());
            case SPAWN_FENCE -> manager.spawnFence((SpawnFencePayload) packet.payload());
        }

        updateCounters.put(((IDLinked) packet.payload()).id(), 0L);
    }

    private void manageUpdate(PacketSubType subtype, DGNPacket packet) {
        long update_count = ((UpdateCounter) packet.payload()).counter();
        if (updateCounters.get(((IDLinked) packet.payload()).id()) > update_count) {
            return;
        }

        switch (subtype) {
            case UPDATE_HERO -> manager.updateHero((UpdateHeroPayload) packet.payload());
            case UPDATE_MOB -> manager.updateMob((UpdateMobPayload) packet.payload());
            case UPDATE_PROJECTILE -> manager.updateProjectile((UpdateProjectilePayload) packet.payload());
        }
        updateCounters.put(((IDLinked) packet.payload()).id(), update_count);

    }

    private void manageRemove(PacketSubType subtype, DGNPacket packet) {
        switch (subtype) {
            case REMOVE_ENTITY -> manager.removeEntity((EntityRemovePayload) packet.payload());
            case REMOVE_PROJECTILE -> manager.removeProjectile(((EntityRemovePayload) packet.payload()));
        }
    }
}
