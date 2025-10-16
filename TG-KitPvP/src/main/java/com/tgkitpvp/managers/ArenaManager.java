package com.tgkitpvp.managers;

import com.tgkitpvp.TGKitPVP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ArenaManager {
    private final TGKitPVP plugin;
    private Location spawnLocation;
    private String arenaWorld;
    private int minX, minZ, maxX, maxZ;
    private boolean arenaEnabled;

    public ArenaManager(TGKitPVP plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        String worldName = plugin.getConfig().getString("spawn.world", "world");
        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            double x = plugin.getConfig().getDouble("spawn.x", 0.5);
            double y = plugin.getConfig().getDouble("spawn.y", 64.0);
            double z = plugin.getConfig().getDouble("spawn.z", 0.5);
            float yaw = (float) plugin.getConfig().getDouble("spawn.yaw", 0.0);
            float pitch = (float) plugin.getConfig().getDouble("spawn.pitch", 0.0);

            spawnLocation = new Location(world, x, y, z, yaw, pitch);
        } else {
            plugin.getLogger().warning("Spawn world '" + worldName + "' not found!");
            spawnLocation = null;
        }

        arenaWorld = plugin.getConfig().getString("settings.arena-world", "world");
        arenaEnabled = plugin.getConfig().getBoolean("arena.enabled", true);
        minX = plugin.getConfig().getInt("arena.min-x", -100);
        minZ = plugin.getConfig().getInt("arena.min-z", -100);
        maxX = plugin.getConfig().getInt("arena.max-x", 100);
        maxZ = plugin.getConfig().getInt("arena.max-z", 100);
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        plugin.getConfig().set("spawn.world", location.getWorld().getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.yaw", location.getYaw());
        plugin.getConfig().set("spawn.pitch", location.getPitch());
        plugin.saveConfig();
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public boolean hasSpawn() {
        return spawnLocation != null;
    }

    public boolean isInArena(Location location) {
        if (!arenaEnabled) {
            return location.getWorld().getName().equals(arenaWorld);
        }

        if (!location.getWorld().getName().equals(arenaWorld)) {
            return false;
        }

        int x = location.getBlockX();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    public boolean isInArena(Player player) {
        return isInArena(player.getLocation());
    }

    public void teleportToSpawn(Player player) {
        if (hasSpawn()) {
            player.teleport(spawnLocation);
        } else {
            plugin.getLogger().warning("Attempted to teleport " + player.getName() + " to spawn, but spawn is not set!");
        }
    }

    public String getArenaWorld() {
        return arenaWorld;
    }

    public boolean isPvPAllowed(Location location) {
        if (plugin.getConfig().getBoolean("arena.pvp-only-in-arena", true)) {
            return isInArena(location);
        }
        return true;
    }
}
