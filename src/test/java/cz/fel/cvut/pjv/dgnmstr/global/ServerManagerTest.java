package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Role;
import cz.fel.cvut.pjv.dgnmstr.network.ServerNetworkManager;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerManagerTest {

    private ServerManager serverManager;
    private ServerNetworkManager mockSnm;
    private PlayerProfile profile;

    @BeforeEach
    void setUp() throws Exception {
        serverManager = new ServerManager(0);
        mockSnm = mock(ServerNetworkManager.class);
        
        Field snmField = ServerManager.class.getDeclaredField("snm");
        snmField.setAccessible(true);
        snmField.set(serverManager, mockSnm);
        
        serverManager.generateNewLevel();
        profile = new PlayerProfile(Role.WARRIOR);
    }

    @Test
    void addProfile() {
        int heroId = serverManager.addProfile(profile);
        Hero hero = (Hero) serverManager.getLc().getLevel().getEntity(heroId);
        
        assertNotNull(hero);
        assertEquals(profile, hero.getProfile());
    }

    @Test
    void updateByProfile() {
        int heroId = serverManager.addProfile(profile);
        Hero hero = (Hero) serverManager.getLc().getLevel().getEntity(heroId);
        
        PlayerProfile updatedProfile = new PlayerProfile(Role.WARRIOR);
        updatedProfile.setGold(150);
        
        serverManager.updateByProfile(heroId, updatedProfile);
        
        assertEquals(150, hero.getProfile().getGold());
    }

    @Test
    void tick() throws Exception {
        serverManager.startLC();
        
        Field runningField = ServerManager.class.getDeclaredField("running");
        runningField.setAccessible(true);
        assertTrue((boolean) runningField.get(serverManager));
        
        serverManager.tick();
        
        verify(mockSnm, atLeastOnce()).sendArenaUpdates();
        verify(mockSnm, atLeastOnce()).sendLevelUpdates();
        verify(mockSnm, atLeastOnce()).sendEntityUpdates();
        verify(mockSnm, atLeastOnce()).sendProjectileUpdates();
        
        serverManager.stopLC();
    }
}