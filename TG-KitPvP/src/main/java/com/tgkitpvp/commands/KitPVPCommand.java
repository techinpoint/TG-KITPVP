package com.tgkitpvp.commands;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.PlayerData;
import com.tgkitpvp.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitPVPCommand implements CommandExecutor, TabCompleter {
    private final TGKitPVP plugin;
    private final MessageUtil messageUtil;

    public KitPVPCommand(TGKitPVP plugin) {
        this.plugin = plugin;
        this.messageUtil = new MessageUtil(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join":
                handleJoin(player);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "reload":
                handleReload(player);
                break;
            case "setspawn":
                handleSetSpawn(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleJoin(Player player) {
        if (!player.hasPermission("kitpvp.use")) {
            messageUtil.sendConfigMessage(player, "no-permission");
            return;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (data.isInArena()) {
            MessageUtil.sendMessage(player, MessageUtil.getPrefix(plugin) + "&cYou are already in the arena!");
            return;
        }

        if (!plugin.getArenaManager().hasSpawn()) {
            messageUtil.sendConfigMessage(player, "spawn-not-set");
            return;
        }

        plugin.getArenaManager().teleportToSpawn(player);
        data.setInArena(true);
        messageUtil.sendConfigMessage(player, "joined-arena");
    }

    private void handleLeave(Player player) {
        if (!player.hasPermission("kitpvp.use")) {
            messageUtil.sendConfigMessage(player, "no-permission");
            return;
        }

        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (!data.isInArena()) {
            messageUtil.sendConfigMessage(player, "not-in-arena");
            return;
        }

        data.setInArena(false);
        player.getInventory().clear();

        for (var effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        messageUtil.sendConfigMessage(player, "left-arena");
    }

    private void handleReload(Player player) {
        if (!player.hasPermission("kitpvp.reload")) {
            messageUtil.sendConfigMessage(player, "no-permission");
            return;
        }

        plugin.reload();
        messageUtil.sendConfigMessage(player, "reload-success");
    }

    private void handleSetSpawn(Player player) {
        if (!player.hasPermission("kitpvp.admin")) {
            messageUtil.sendConfigMessage(player, "no-permission");
            return;
        }

        plugin.getArenaManager().setSpawnLocation(player.getLocation());
        MessageUtil.sendMessage(player, MessageUtil.getPrefix(plugin) + "&aSpawn location set!");
    }

    private void sendHelp(Player player) {
        MessageUtil.sendMessage(player, "&8&m----&r &c&lTG-KITPVP Help &8&m----&r");
        MessageUtil.sendMessage(player, "&e/kitpvp join &7- Join the arena");
        MessageUtil.sendMessage(player, "&e/kitpvp leave &7- Leave the arena");
        MessageUtil.sendMessage(player, "&e/kit select <kit> &7- Select a kit");
        MessageUtil.sendMessage(player, "&e/kit list &7- List all kits");
        MessageUtil.sendMessage(player, "&e/leaderboard &7- View top players");

        if (player.hasPermission("kitpvp.admin")) {
            MessageUtil.sendMessage(player, "&c&lAdmin Commands:");
            MessageUtil.sendMessage(player, "&e/kitpvp setspawn &7- Set spawn location");
            MessageUtil.sendMessage(player, "&e/kitpvp reload &7- Reload configuration");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("join", "leave", "reload", "setspawn"));
        }

        return completions;
    }
}
