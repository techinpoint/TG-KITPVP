package com.tgkitpvp;

import com.tgkitpvp.commands.KitCommand;
import com.tgkitpvp.commands.KitPVPCommand;
import com.tgkitpvp.commands.LeaderboardCommand;
import com.tgkitpvp.commands.StatsCommand;
import com.tgkitpvp.listeners.ArenaListener;
import com.tgkitpvp.listeners.PlayerListener;
import com.tgkitpvp.managers.ArenaManager;
import com.tgkitpvp.managers.DataManager;
import com.tgkitpvp.managers.KitManager;
import com.tgkitpvp.managers.EconomyManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TGKitPVP extends JavaPlugin {

    private static TGKitPVP instance;
    private KitManager kitManager;
    private ArenaManager arenaManager;
    private DataManager dataManager;
    private EconomyManager economyManager;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        kitManager = new KitManager(this);
        arenaManager = new ArenaManager(this);
        dataManager = new DataManager(this);
        economyManager = new EconomyManager(this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
        }

        registerCommands();
        registerListeners();

        dataManager.loadData();

        getLogger().info("TG-KITPVP has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveData();
        }
        getLogger().info("TG-KITPVP has been disabled successfully!");
    }

    private void registerCommands() {
        getCommand("kitpvp").setExecutor(new KitPVPCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("stats").setExecutor(new StatsCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ArenaListener(this), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        getLogger().info("Vault economy integration enabled!");
        return economy != null;
    }

    public void reload() {
        reloadConfig();
        kitManager.loadKits();
        arenaManager.reload();
        dataManager.saveData();
        dataManager.loadData();
    }

    public static TGKitPVP getInstance() {
        return instance;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean hasEconomy() {
        return economy != null && getConfig().getBoolean("economy.enabled", false);
    }
}
