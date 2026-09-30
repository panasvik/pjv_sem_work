package cz.fel.cvut.pjv.dgnmstr.network;

import cz.fel.cvut.pjv.dgnmstr.dungeon.Level;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.projectile.Projectile;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.global.Inputs;
import cz.fel.cvut.pjv.dgnmstr.global.ServerManager;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
/**
 * Server side of the UDP networking — receives client packets
 * and dispatches them to game logic.
 */
public class ServerNetworkManager {
    private static final Logger log = Logger.getLogger(ServerNetworkManager.class.getName());
    private static final int BUFFER_SIZE = 4096;

    private DatagramSocket udpSocket;
    private final ServerManager serverManager;

    private final Map<InetSocketAddress, PlayerProfile> clients = new ConcurrentHashMap<>();
    private final Map<InetSocketAddress, Integer> clientHeroIds = new ConcurrentHashMap<>();
    private final Map<Integer, Long> updateCounters = new ConcurrentHashMap<>();
    private final Set<Integer> trackedEntityIds = new HashSet<>();
    private final Set<Integer> trackedProjectileIds = new HashSet<>();




    private boolean isRunning = false;
    private boolean isGameStarted = false;
    private final int udpPort;

    /**
     * Creates a new ServerNetworkManager.
     * @param udpPort udp port
     * @param serverManager server manager
     */
    public ServerNetworkManager(int udpPort, ServerManager serverManager) {
        this.udpPort = udpPort;
        this.serverManager = serverManager;
    }

    public void start() {
        try {
            udpSocket = new DatagramSocket(udpPort);
            isRunning = true;

            Thread udpListenThread = new Thread(this::listenUdpLoop);
            udpListenThread.setDaemon(true);
            udpListenThread.start();

            log.info("ServerNetworkManager started on UDP port: " + udpPort);
        } catch (IOException e) {
            log.info("Failed to start server: " + e.getMessage());
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
            log.info("Failed to broadcast UDP packet: " + e.getMessage());
        }
    }

    private void listenUdpLoop() {
        byte[] buffer = new byte[BUFFER_SIZE];
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

    private boolean isNewClient(DGNPacket packet, InetSocketAddress source) {
        return (packet.type() == PacketType.PLAYER_PROFILE &&
                packet.payload() instanceof PlayerProfilePayload &&
                !clients.containsKey(source) && !isGameStarted);
    }

    public void sendSeeds() {
        for (Map.Entry<InetSocketAddress, PlayerProfile> entry : clients.entrySet()) {
            sendSeed(entry.getKey());
        }
    }

    private void sendSeed(InetSocketAddress source) {
        try {
            byte[] data = SerializationUtils.serialize(new DGNPacket(PacketType.SEED, new LevelSeedPayload(serverManager.getSeed())));
            DatagramPacket idPacket = new DatagramPacket(data, data.length, source.getAddress(), source.getPort());
            udpSocket.send(idPacket);
        } catch (IOException e) {
            log.severe("Unable to send seed to " + source);
            throw new RuntimeException(e);
        }
    }

    private void sendClientId(InetSocketAddress source, int id) {
        try {
            byte[] data = SerializationUtils.serialize(new DGNPacket(PacketType.CLIENT_ID, new ClientID(id)));
            DatagramPacket idPacket = new DatagramPacket(data, data.length, source.getAddress(), source.getPort());
            udpSocket.send(idPacket);
        } catch (IOException e) {
            log.severe("Unable to send client id to " + source);

            throw new RuntimeException(e);
        }
    }

    private void handleIncomingPacket(DGNPacket packet, InetSocketAddress source) {
        if (isNewClient(packet, source)) {
            PlayerProfile profile = ((PlayerProfilePayload) packet.payload()).profile();
            clients.put(source, profile);
            log.info("New client registered with profile from: " + source);
        }

        if (!clients.containsKey(source)) {
            log.info("Ignored packet from unknown client: " + source);
            return;
        }

        handleClientUpdatePacket(packet, source);
    }

    private void handleClientUpdatePacket(DGNPacket packet, InetSocketAddress source) {
        switch (packet.type()) {
            case UPDATE -> {
                if (packet.subType() != PacketSubType.HERO) {
                    return;
                }
                handleNewInputs(source, (Inputs) packet.payload());
            }
            case PROFILE_UPDATE -> handleProfileUpdate(source, (PlayerProfilePayload) packet.payload());
            case USE_POTION -> handleUsePotion((UsePotionPayload) packet.payload());
            default -> log.info("Received packet: " + packet.type() + " from " + source);
        }
    }

    private void handleProfileUpdate(InetSocketAddress source, PlayerProfilePayload payload) {
        Integer heroId = clientHeroIds.get(source);
        System.out.println("got plrofile with " + payload.profile().getGold() + " gold in it");
        if (heroId != null) {
            clients.get(source).mergeProfileChanges(payload.profile());
            serverManager.updateByProfile(heroId, payload.profile());
        }
    }

    private void handleUsePotion(UsePotionPayload payload) {
        int heroId = payload.heroId();
        if (serverManager != null) {
            serverManager.useItemByType(heroId, payload.potionType());
        }
    }

    private void handleNewInputs(InetSocketAddress source, Inputs inputs) {
        Integer heroId = clientHeroIds.get(source);
        if (heroId != null) {
            serverManager.applyInputs(heroId, inputs);
        }
    }

    public void disconnectClient(InetSocketAddress clientAddress) {
        if (clients.remove(clientAddress) != null) {
            clientHeroIds.remove(clientAddress);
            log.info("Client disconnected/removed: " + clientAddress);
        }
    }

    public void stop() {
        isRunning = false;
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
        for (InetSocketAddress clientAddress : clients.keySet()) {
            disconnectClient(clientAddress);
        }
        clients.clear();
        clientHeroIds.clear();
        isGameStarted = false;
        log.info("Server stopped.");
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

    public void handleUpdateEjection(Hero hero) {
        long newCount = updateCounters.get(hero.getId()) + 1;
        updateCounters.put(hero.getId(), newCount);
        broadcast(new DGNPacket(PacketType.UPDATE, PacketSubType.HERO,
                new UpdateHeroPayload(
                        hero.getId(),
                        hero.getX(),
                        hero.getY(),
                        hero.getHp(),
                        hero.getGold(),
                        hero.getLookX(),
                        hero.getLookY(),
                        hero.getAttackCount(),
                        hero.isUltimateActive(),
                        newCount
                )));
        if (hero.getMaxHP() != hero.getProfile().getPlayerStats().hp()) {
            broadcast(new DGNPacket(PacketType.SYNC_STATS,
                    new SyncPlayerStatsPayload(hero.getId(), hero.getProfile().getPlayerStats())));
        }
    }

    public void handleUpdateEjection(Mob mob) {
        long newCount = updateCounters.get(mob.getId()) + 1;
        updateCounters.put(mob.getId(), newCount);
        broadcast(new DGNPacket(PacketType.UPDATE, PacketSubType.MOB,
                new UpdateMobPayload(mob.getId(), mob.getX(), mob.getY(), mob.getHp(),
                        mob.getLookX(), mob.getLookY(), mob.getAttackCount(), newCount)));
    }

    public void sendEntityUpdates() {
        List<Entity> entityList = serverManager.getLc().getLevel().getAllEntities();
        Set<Integer> currentEntityIds = new HashSet<>();
        for (Entity entity : entityList) {
            currentEntityIds.add(entity.getId());
            switch (entity) {
                case Hero hero -> handleUpdateEjection(hero);
                case Mob mob -> handleUpdateEjection(mob);
                default -> {} // nothing to update for those
            }
        }

        for (Integer removedEntityId : trackedEntityIds) {
            if (!currentEntityIds.contains(removedEntityId)) {
                broadcast(new DGNPacket(PacketType.REMOVE, PacketSubType.ENTITY, new EntityRemovePayload(removedEntityId)));
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
            sendProjectilePacket(projectile);
        }

        for (Integer removedProjectileId : trackedProjectileIds) {
            if (!currentProjectileIds.contains(removedProjectileId)) {
                broadcast(new DGNPacket(PacketType.REMOVE, PacketSubType.PROJECTILE, new EntityRemovePayload(removedProjectileId)));
            }
        }

        trackedProjectileIds.clear();
        trackedProjectileIds.addAll(currentProjectileIds);
    }


    public Set<Integer> getTrackedEntityIds() {
        return trackedEntityIds;
    }

    public Set<Integer> getTrackedProjectileIds() {
        return trackedProjectileIds;
    }

    private void sendProjectilePacket(Projectile projectile) {
        long newCount = updateCounters.get(projectile.getId()) + 1;
        updateCounters.put(projectile.getId(), newCount);
        broadcast(new DGNPacket(PacketType.UPDATE, PacketSubType.PROJECTILE,
                new UpdateProjectilePayload(
                        projectile.getId(),
                        projectile.getX(),
                        projectile.getY(),
                        projectile.isAlive(),
                        newCount
                )));
    }

    public void sendIDs() {
        for (Map.Entry<InetSocketAddress, PlayerProfile> entry: clients.entrySet()) {
            InetSocketAddress source = entry.getKey();
            int id = serverManager.addProfile(entry.getValue());
            clientHeroIds.put(source, id);
            sendClientId(source, id);
        }
    }

    public void setNewCounter(int id) {
        updateCounters.put(id, 0L);
    }

}
