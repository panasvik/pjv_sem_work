package cz.fel.cvut.pjv.dgnmstr.dungeon.controllers;

import cz.fel.cvut.pjv.dgnmstr.entity.Entity;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.Boss;
import cz.fel.cvut.pjv.dgnmstr.entity.boss.BossStats;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Chest;
import cz.fel.cvut.pjv.dgnmstr.entity.map.Grate;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.Mob;
import cz.fel.cvut.pjv.dgnmstr.entity.mobs.MobStats;
import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

public interface EntitySpawner {
        void spawnProjectile(Entity master, int radius, int damage, double deltaX, double deltaY, double velocity);
        void spawnGold(double x, double y, int amount);
        Mob spawnMob(double x, double y, MobStats stats, int mult);
        Boss spawnBoss(double x, double y, BossStats stats);
        Grate spawnGrate(double x, double y);
        Chest spawnChest(double x, double y, Inventory inventory);
        Hero spawnHero(double x, double y, PlayerProfile profile);

}
