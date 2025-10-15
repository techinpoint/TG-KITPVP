package com.tgkitpvp.managers;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {
    private final TGKitPVP plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(TGKitPVP plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        setupDataFile();
    }

    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml!");
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadData() {
        playerDataMap.clear();
        if (!dataFile.exists()) {
            return;
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.contains("players")) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    String path = "players." + uuidString + ".";

                    String playerName = dataConfig.getString(path + "name", "Unknown");
                    int kills = dataConfig.getInt(path + "kills", 0);
                    int deaths = dataConfig.getInt(path + "deaths", 0);
                    int currentStreak = dataConfig.getInt(path + "current-streak", 0);
                    int bestStreak = dataConfig.getInt(path + "best-streak", 0);

                    PlayerData data = new PlayerData(uuid, playerName);
                    data.setKills(kills);
                    data.setDeaths(deaths);
                    data.setCurrentStreak(currentStreak);
                    data.setBestStreak(bestStreak);

                    playerDataMap.put(uuid, data);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata.yml: " + uuidString);
                }
            }
        }

        plugin.getLogger().info("Loaded data for " + playerDataMap.size() + " players");
    }

    public void saveData() {
        for (PlayerData data : playerDataMap.values()) {
            String path = "players." + data.getUuid().toString() + ".";
            dataConfig.set(path + "name", data.getPlayerName());
            dataConfig.set(path + "kills", data.getKills());
            dataConfig.set(path + "deaths", data.getDeaths());
            dataConfig.set(path + "current-streak", data.getCurrentStreak());
            dataConfig.set(path + "best-streak", data.getBestStreak());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, k -> {
            Player player = plugin.getServer().getPlayer(uuid);
            String name = player != null ? player.getName() : "Unknown";
            return new PlayerData(uuid, name);
        });
    }

    public PlayerData getPlayerData(Player player) {
        PlayerData data = getPlayerData(player.getUniqueId());
        data.setPlayerName(player.getName());
        return data;
    }

    public List<PlayerData> getTopPlayers(int limit) {
        return playerDataMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getKills(), a.getKills()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public Collection<PlayerData> getAllPlayerData() {
        return new ArrayList<>(playerDataMap.values());
    }

    public void savePlayerData(PlayerData data) {
        playerDataMap.put(data.getUuid(), data);
    }
}
