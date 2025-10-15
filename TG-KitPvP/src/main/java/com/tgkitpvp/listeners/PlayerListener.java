package com.tgkitpvp.listeners;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.PlayerData;
import com.tgkitpvp.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    private final TGKitPVP plugin;

    public PlayerListener(TGKitPVP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (plugin.getConfig().getBoolean("settings.spawn-on-join", true)) {
            if (plugin.getArenaManager().hasSpawn()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getArenaManager().teleportToSpawn(player);
                        data.setInArena(true);
                    }
                }.runTaskLater(plugin, 5L);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player);
        data.setInArena(false);
        plugin.getDataManager().saveData();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        PlayerData victimData = plugin.getDataManager().getPlayerData(victim);

        if (!victimData.isInArena()) {
            return;
        }

        victimData.addDeath();

        if (killer != null && killer != victim) {
            PlayerData killerData = plugin.getDataManager().getPlayerData(killer);

            if (killerData.isInArena()) {
                killerData.addKill();

                double reward = plugin.getConfig().getDouble("economy.kill-reward", 10.0);
                if (plugin.hasEconomy()) {
                    plugin.getEconomyManager().rewardKill(killer, killerData);
                    messageUtil.sendConfigMessage(killer, "kill",
                            new String[]{"player", "reward"},
                            new String[]{victim.getName(), String.valueOf(reward)});
                } else {
                    messageUtil.sendConfigMessage(killer, "kill",
                            new String[]{"player", "reward"},
                            new String[]{victim.getName(), "0"});
                }

                if (killerData.getCurrentStreak() > 1 && killerData.getCurrentStreak() % 5 == 0) {
                    messageUtil.sendConfigMessage(killer, "killstreak", "streak",
                            String.valueOf(killerData.getCurrentStreak()));
                }
            }
        }

        if (plugin.hasEconomy()) {
            plugin.getEconomyManager().penalizeDeath(victim);
        }

        messageUtil.sendConfigMessage(victim, "death", "player",
                killer != null ? killer.getName() : "Unknown");

        event.getDrops().clear();
        event.setDroppedExp(0);

        int respawnDelay = plugin.getConfig().getInt("settings.auto-respawn-delay", 20);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (victim.isDead()) {
                    victim.spigot().respawn();
                }
            }
        }.runTaskLater(plugin, respawnDelay);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (data.isInArena() && plugin.getConfig().getBoolean("settings.spawn-on-death", true)) {
            if (plugin.getArenaManager().hasSpawn()) {
                event.setRespawnLocation(plugin.getArenaManager().getSpawnLocation());

                if (data.getCurrentKit() != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getKitManager().giveKit(player, data.getCurrentKit());
                        }
                    }.runTaskLater(plugin, 1L);
                }
            }
        }
    }

    private final MessageUtil messageUtil = new MessageUtil(plugin);
}
