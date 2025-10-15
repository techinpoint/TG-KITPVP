package com.tgkitpvp.managers;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.PlayerData;
import org.bukkit.entity.Player;

public class EconomyManager {
    private final TGKitPVP plugin;

    public EconomyManager(TGKitPVP plugin) {
        this.plugin = plugin;
    }

    public void rewardKill(Player killer, PlayerData killerData) {
        if (!plugin.hasEconomy()) {
            return;
        }

        double baseReward = plugin.getConfig().getDouble("economy.kill-reward", 10.0);
        double streakBonus = plugin.getConfig().getDouble("economy.streak-bonus", 2.0);

        double totalReward = baseReward;
        if (killerData.getCurrentStreak() > 1) {
            totalReward += (killerData.getCurrentStreak() - 1) * streakBonus;
        }

        plugin.getEconomy().depositPlayer(killer, totalReward);
    }

    public void penalizeDeath(Player player) {
        if (!plugin.hasEconomy()) {
            return;
        }

        double penalty = plugin.getConfig().getDouble("economy.death-penalty", 5.0);
        double balance = plugin.getEconomy().getBalance(player);

        if (balance >= penalty) {
            plugin.getEconomy().withdrawPlayer(player, penalty);
        }
    }

    public double getBalance(Player player) {
        if (!plugin.hasEconomy()) {
            return 0;
        }
        return plugin.getEconomy().getBalance(player);
    }

    public boolean hasEnough(Player player, double amount) {
        if (!plugin.hasEconomy()) {
            return true;
        }
        return plugin.getEconomy().has(player, amount);
    }

    public void withdraw(Player player, double amount) {
        if (plugin.hasEconomy()) {
            plugin.getEconomy().withdrawPlayer(player, amount);
        }
    }

    public void deposit(Player player, double amount) {
        if (plugin.hasEconomy()) {
            plugin.getEconomy().depositPlayer(player, amount);
        }
    }
}
