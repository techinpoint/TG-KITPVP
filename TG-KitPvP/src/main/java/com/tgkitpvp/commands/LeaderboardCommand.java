package com.tgkitpvp.commands;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.PlayerData;
import com.tgkitpvp.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaderboardCommand implements CommandExecutor {
    private final TGKitPVP plugin;
    private final MessageUtil messageUtil;

    public LeaderboardCommand(TGKitPVP plugin) {
        this.plugin = plugin;
        this.messageUtil = new MessageUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kitpvp.use")) {
            messageUtil.sendConfigMessage(sender, "no-permission");
            return true;
        }

        List<PlayerData> topPlayers = plugin.getDataManager().getTopPlayers(10);

        String header = plugin.getConfig().getString("messages.leaderboard-header", "&8&m----&r &c&lTop 10 Players &8&m----&r");
        MessageUtil.sendMessage(sender, header);

        if (topPlayers.isEmpty()) {
            MessageUtil.sendMessage(sender, "&7No player data available yet.");
            return true;
        }

        int rank = 1;
        for (PlayerData data : topPlayers) {
            String entry = plugin.getConfig().getString("messages.leaderboard-entry", "&e#{rank} &f{player} &7- &a{kills} kills");
            entry = entry.replace("{rank}", String.valueOf(rank))
                    .replace("{player}", data.getPlayerName())
                    .replace("{kills}", String.valueOf(data.getKills()))
                    .replace("{deaths}", String.valueOf(data.getDeaths()))
                    .replace("{kd}", String.format("%.2f", data.getKDRatio()))
                    .replace("{streak}", String.valueOf(data.getBestStreak()));

            MessageUtil.sendMessage(sender, entry);
            rank++;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerData playerData = plugin.getDataManager().getPlayerData(player);

            MessageUtil.sendMessage(sender, "");
            MessageUtil.sendMessage(sender, "&7Your Stats: &a" + playerData.getKills() + " kills &7| &c" +
                    playerData.getDeaths() + " deaths &7| &eK/D: " + String.format("%.2f", playerData.getKDRatio()));
        }

        return true;
    }
}
