package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.Projectile;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ServerNetworkManager {
    private static final Logger log = Logger.getLogger(ServerNetworkManager.class.getName());

    private DatagramSocket udpSocket;
    private ServerManager serverManager;
    private long seed;

    private final Map<InetSocketAddress, PlayerProfile> clients = new ConcurrentHashMap<>();
    private final Map<InetSocketAddress, Integer> clientHeroIds = new ConcurrentHashMap<>();
    private static final Inputs EMPTY_INPUTS =
            new Inputs(false, false, false, false, false, false, false, 0, 0);
    private final Map<Integer, Inputs> lastInputsByHeroId = new ConcurrentHashMap<>();
    private final Set<Integer> trackedEntityIds = new HashSet<>();
    private final Set<Integer> trackedProjectileIds = new HashSet<>();




    private boolean isRunning = false;
    private boolean isGameStarted = false;
    private final int udpPort;

    public ServerNetworkManager(int udpPort) {
        this.udpPort = udpPort;
        serverManager = new ServerManager(this);
        Random random = new Random();
        seed = random.nextLong();
        serverManager.generateNewLevel(seed);

    }

    public void start() {
        try {
            udpSocket = new DatagramSocket(udpPort);
            isRunning = true;

            Thread udpListenThread = new Thread(this::listenUdpLoop, "UDP-Listen-Thread");
            udpListenThread.start();

            System.out.println("ServerNetworkManager started on UDP port: " + udpPort);
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }

    public void broadcast(DGNPacket packet) {
        if (!isRunning) return;
        broadcastUDP(packet);
    }

    private void broadcastUDP(DGNPacket packet) {
        try {
            byte[] data = SerializationUtils.serialize(packet);

            for (InetSocketAddress clientAddress : clients.keySet()) {
                DatagramPacket udpPacket = new DatagramPacket(
                        data, data.length, clientAddress.getAddress(), clientAddress.getPort()
                );
                udpSocket.send(udpPacket);
            }
        } catch (IOException e) {
            System.out.println("Failed to broadcast UDP packet: " + e.getMessage());
        }
    }

    private void listenUdpLoop() {
        byte[] buffer = new byte[4096];
        while (isRunning && !udpSocket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);

                InetSocketAddress senderAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());
                DGNPacket dgnPacket = (DGNPacket) SerializationUtils.deserialize(packet.getData());

                handleIncomingPacket(dgnPacket, senderAddress);

            } catch (Exception e) {
                if (isRunning) log.warning("Error reading UDP: " + e.getMessage());
            }
        }
    }

    private void handleIncomingPacket(DGNPacket packet, InetSocketAddress source) {
        if (packet.type() == PacketType.PLAYER_PROFILE &&
                packet.payload() instanceof PlayerProfilePayload payload &&
                !clients.containsKey(source) && !isGameStarted) {
            try {
                byte[] data = SerializationUtils.serialize(new DGNPacket(PacketType.SEED, new LevelSeedPayload(seed)));
                DatagramPacket idPacket = new DatagramPacket(data, data.length, source.getAddress(), source.getPort());
                udpSocket.send(idPacket);
            } catch (IOException e) {
                log.severe("Unable to send seed to " + source);
                throw new RuntimeException(e);
            }
            clients.put(source, payload.profile());
            int id = serverManager.addProfile(payload.profile());
            clientHeroIds.put(source, id);
            try {
                byte[] data = SerializationUtils.serialize(new DGNPacket(PacketType.CLIENT_ID, new ClientID(id)));
                DatagramPacket idPacket = new DatagramPacket(data, data.length, source.getAddress(), source.getPort());
                udpSocket.send(idPacket);
            } catch (IOException e) {
                log.severe("Unable to send client id to " + source);

                throw new RuntimeException(e);
            }

            System.out.println("New client registered with profile from: " + source);
            System.out.println(source);
        }

        if (clients.containsKey(source)) {
            if (packet.type() == PacketType.UPDATE_HERO && packet.payload() instanceof Inputs inputs) {
                Integer heroId = clientHeroIds.get(source);
                if (heroId != null) {
                    serverManager.applyInputs(heroId, inputs);
                }
            } else {
                System.out.println("Received packet: " + packet.type() + " from " + source);
            }
        } else {
            System.out.println("Ignored packet from unknown client: " + source);
        }
    }

    public void disconnectClient(InetSocketAddress clientAddress) {
        if (clients.remove(clientAddress) != null) {
            clientHeroIds.remove(clientAddress);
            System.out.println("Client disconnected/removed: " + clientAddress);
        }
    }

    public void stop() {
        isRunning = false;
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
        clients.clear();
        clientHeroIds.clear();
        serverManager.stopLC();
        isGameStarted = false;
        System.out.println("Server stopped.");
    }

    public Map<InetSocketAddress, PlayerProfile> getClients() {
        return clients;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
        if (isGameStarted) {
            serverManager.startLC();
        }
        else {
            serverManager.stopLC();
        }
    }

    public void sendLevelUpdates() {
        Level level = serverManager.getLc().getLevel();
        LevelState currentLevelState = level.getState();
        if (serverManager.getLastLevelState() != currentLevelState) {
            broadcast(new DGNPacket(PacketType.LEVEL_STATE, new LevelStatePayload(currentLevelState)));
            serverManager.setLastLevelState(currentLevelState);
        }
    }

    public void sendArenaUpdates() {
        ArenaState currentArenaState = serverManager.getLc().getLevel().getCurArena() != null ? serverManager.getLc().getLevel().getCurArena().getState() : null;
        if (currentArenaState != null && serverManager.getLastArenaState() != currentArenaState) {
            broadcast(new DGNPacket(PacketType.ARENA_STATE, new ArenaStatePayload(currentArenaState)));
            serverManager.setLastArenaState(currentArenaState);
        }
    }

    public void sendEntityUpdates() {
        Level level = serverManager.getLc().getLevel();
        Set<Integer> currentEntityIds = new HashSet<>();
        for (Entity entity : level.getAllEntities()) {
            currentEntityIds.add(entity.getId());
            if (entity instanceof Hero hero) {
                Inputs inputs = lastInputsByHeroId.getOrDefault(hero.getId(), EMPTY_INPUTS);
                broadcast(new DGNPacket(PacketType.UPDATE_HERO,
                        new UpdateHeroPayload(
                                hero.getId(),
                                hero.getX(),
                                hero.getY(),
                                hero.getHp(),
                                inputs.mouseX(),
                                inputs.mouseY(),
                                inputs.attack(),
                                inputs.ult(),
                                inputs.interact()
                        )));
                if (hero.getMaxHP() != hero.getProfile().getPlayerStats().hp()) {
                    broadcast(new DGNPacket(PacketType.SYNC_STATS,
                            new SyncPlayerStatsPayload(hero.getId(), hero.getProfile().getPlayerStats())));
                }

            } else if (entity instanceof Mob mob) {
                broadcast(new DGNPacket(PacketType.UPDATE_MOB,
                        new UpdateMobPayload(mob.getId(), mob.getX(), mob.getY(), mob.getHp())));
            }
        }

        for (Integer removedEntityId : trackedEntityIds) {
            if (!currentEntityIds.contains(removedEntityId)) {
                broadcast(new DGNPacket(PacketType.ENTITY_REMOVE, new EntityRemovePayload(removedEntityId)));
            }
        }
        trackedEntityIds.clear();
        trackedEntityIds.addAll(currentEntityIds);
    }

    public void sendProjectileUpdates() {
        Level level = serverManager.getLc().getLevel();
        Set<Integer> currentProjectileIds = new HashSet<>();
        for (Projectile projectile : level.getProjectiles()) {
            currentProjectileIds.add(projectile.getId());
            broadcast(new DGNPacket(PacketType.UPDATE_PROJECTILE,
                    new UpdateProjectilePayload(
                            projectile.getId(),
                            projectile.getX(),
                            projectile.getY(),
                            projectile.isAlive()
                    )));
        }

        for (Integer removedProjectileId : trackedProjectileIds) {
            if (!currentProjectileIds.contains(removedProjectileId)) {
                broadcast(new DGNPacket(PacketType.REMOVE_PROJECTILE, new EntityRemovePayload(removedProjectileId)));
            }
        }
        trackedProjectileIds.clear();
        trackedProjectileIds.addAll(currentProjectileIds);
    }

    public Map<Integer, Inputs> getLastInputsByHeroId() {
        return lastInputsByHeroId;
    }

    public Set<Integer> getTrackedEntityIds() {
        return trackedEntityIds;
    }

    public Set<Integer> getTrackedProjectileIds() {
        return trackedProjectileIds;
    }
}