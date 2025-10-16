package com.tgkitpvp.commands;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.PlayerData;
import com.tgkitpvp.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatsCommand implements CommandExecutor, TabCompleter {
    private final TGKitPVP plugin;
    private final MessageUtil messageUtil;

    public StatsCommand(TGKitPVP plugin) {
        this.plugin = plugin;
        this.messageUtil = new MessageUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kitpvp.use")) {
            messageUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        Player target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                MessageUtil.sendMessage(sender, "&cYou must specify a player name from console!");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtil.sendMessage(sender, "&cPlayer not found!");
                return true;
            }
        }

        PlayerData data = plugin.getDataManager().getPlayerData(target);
        
        MessageUtil.sendMessage(sender, "&8&m----&r &c&l" + target.getName() + "'s Stats &8&m----&r");
        MessageUtil.sendMessage(sender, "&eKills: &a" + data.getKills());
        MessageUtil.sendMessage(sender, "&eDeaths: &c" + data.getDeaths());
        MessageUtil.sendMessage(sender, "&eK/D Ratio: &6" + String.format("%.2f", data.getKDRatio()));
        MessageUtil.sendMessage(sender, "&eCurrent Streak: &b" + data.getCurrentStreak());
        MessageUtil.sendMessage(sender, "&eBest Streak: &d" + data.getBestStreak());
        
        if (data.getCurrentKit() != null) {
            MessageUtil.sendMessage(sender, "&eCurrent Kit: &f" + data.getCurrentKit());
        }
        
        if (plugin.hasEconomy()) {
            double balance = plugin.getEconomyManager().getBalance(target);
            MessageUtil.sendMessage(sender, "&eBalance: &2$" + String.format("%.2f", balance));
        }
        
        MessageUtil.sendMessage(sender, "&eIn Arena: " + (data.isInArena() ? "&aYes" : "&cNo"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
