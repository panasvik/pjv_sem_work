package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.dungeon.service.ArenaState;
import cz.fel.cvut.pjv.dgnmstr.dungeon.service.LevelState;
import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Role;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;
import cz.fel.cvut.pjv.dgnmstr.item.weapon.BasicWarriorSword;
import cz.fel.cvut.pjv.dgnmstr.network.ClientNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.network.payloads.*;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientManagerTest {

    private ClientManager clientManager;
    private ClientNetworkManager networkManager;
    private PlayerProfile profile;

    @BeforeEach
    void setUp() throws Exception {
        profile = new PlayerProfile(Role.WARRIOR);
        clientManager = new ClientManager("127.0.0.1", 0, profile);
        networkManager = mock(ClientNetworkManager.class);
        
        java.lang.reflect.Field nmField = ClientManager.class.getDeclaredField("networkManager");
        nmField.setAccessible(true);
        nmField.set(clientManager, networkManager);
        
        clientManager.makeNewLC(12345L);
    }

    @Test
    void addPlayer() {
        SpawnHeroPayload payload = new SpawnHeroPayload(1, 100.0, 100.0, profile);
        clientManager.addPlayer(payload);
        assertNotNull(clientManager.getLc().getLevel().getEntity(1));
    }

    @Test
    void setLocalHero() {
        SpawnHeroPayload payload = new SpawnHeroPayload(1, 100.0, 100.0, profile);
        clientManager.addPlayer(payload);
        clientManager.setLocalHero(1);
        assertNotNull(clientManager.getLocalHero());
        assertTrue(clientManager.isReady());
    }

    @Test
    void spawnMob() {
        SpawnMobPayload payload = new SpawnMobPayload(2, MobStats.ZOMBIE, 150.0, 150.0, 1);
        clientManager.spawnMob(payload);
        assertNotNull(clientManager.getLc().getLevel().getEntity(2));
    }

    @Test
    void updateMob() {
        SpawnMobPayload spawnPayload = new SpawnMobPayload(2, MobStats.ZOMBIE, 150.0, 150.0, 1);
        clientManager.spawnMob(spawnPayload);
        UpdateMobPayload updatePayload = new UpdateMobPayload(2, 200.0, 200.0, 10, 1);
        clientManager.updateMob(updatePayload);
        Entity mob = clientManager.getLc().getLevel().getEntity(2);
        assertEquals(200.0, mob.getX());
        assertEquals(200.0, mob.getY());
        assertEquals(10, mob.getHp());
    }

    @Test
    void removeEntity() {
        SpawnMobPayload spawnPayload = new SpawnMobPayload(2, MobStats.ZOMBIE, 150.0, 150.0, 1);
        clientManager.spawnMob(spawnPayload);
        EntityRemovePayload removePayload = new EntityRemovePayload(2);
        clientManager.removeEntity(removePayload);
        assertNull(clientManager.getLc().getLevel().getEntity(2));
    }


    @Test
    void updateLevelState() {
        LevelStatePayload payload = new LevelStatePayload(LevelState.PLAYING);
        clientManager.updateLevelState(payload);
        assertEquals(LevelState.PLAYING, clientManager.getLc().getLevel().getState());
    }

    @Test
    void updateArenaState() {
        clientManager.getLc().startLevel();
        ArenaStatePayload payload = new ArenaStatePayload(ArenaState.CLEARED);
        clientManager.updateArenaState(payload);
        assertEquals(ArenaState.CLEARED, clientManager.getLc().getLevel().getCurArena().getState());
    }

    @Test
    void syncStats() {
        SpawnHeroPayload spawnPayload = new SpawnHeroPayload(1, 100.0, 100.0, profile);
        clientManager.addPlayer(spawnPayload);
        PlayerStats stats = new PlayerStats(100, 100, 100, 100, 100, 100, 0 );
        SyncPlayerStatsPayload payload = new SyncPlayerStatsPayload(1, stats);
        clientManager.syncStats(payload);
        Hero hero = (Hero) clientManager.getLc().getLevel().getEntity(1);
        assertEquals(100, hero.getMaxHP());
    }


    @Test
    void useItem() {
        SpawnHeroPayload spawnPayload = new SpawnHeroPayload(1, 100.0, 100.0, profile);
        clientManager.addPlayer(spawnPayload);
        clientManager.setLocalHero(1);
        BasicWarriorSword sword = new BasicWarriorSword();
        boolean result = clientManager.useItem(sword);
        assertTrue(result);
        verify(networkManager).sendProfileUpdate(profile);
    }
}
