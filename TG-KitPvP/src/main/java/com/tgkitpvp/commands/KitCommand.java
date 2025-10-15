package com.tgkitpvp.commands;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.Kit;
import com.tgkitpvp.models.PlayerData;
import com.tgkitpvp.utils.ItemBuilder;
import com.tgkitpvp.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KitCommand implements CommandExecutor, TabCompleter {
    private final TGKitPVP plugin;
    private final MessageUtil messageUtil;

    public KitCommand(TGKitPVP plugin) {
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

        if (!player.hasPermission("kitpvp.use")) {
            messageUtil.sendConfigMessage(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            openKitGUI(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "select":
                if (args.length < 2) {
                    MessageUtil.sendMessage(player, MessageUtil.getPrefix(plugin) + "&cUsage: /kit select <kitname>");
                    return true;
                }
                handleSelectKit(player, args[1]);
                break;
            case "list":
                handleListKits(player);
                break;
            case "create":
                if (!player.hasPermission("kitpvp.create")) {
                    messageUtil.sendConfigMessage(player, "no-permission");
                    return true;
                }
                MessageUtil.sendMessage(player, MessageUtil.getPrefix(plugin) + "&cKit creation via command is not yet implemented. Edit config.yml directly.");
                break;
            default:
                openKitGUI(player);
                break;
        }

        return true;
    }

    private void handleSelectKit(Player player, String kitName) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (!data.isInArena()) {
            messageUtil.sendConfigMessage(player, "not-in-arena");
            return;
        }

        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            messageUtil.sendConfigMessage(player, "kit-not-found");
            return;
        }

        if (!kit.getPermission().isEmpty() && !player.hasPermission(kit.getPermission())) {
            messageUtil.sendConfigMessage(player, "no-permission");
            return;
        }

        long cooldown = plugin.getConfig().getLong("settings.kit-cooldown", 30) * 1000;
        long lastChange = data.getLastKitChange();
        long currentTime = System.currentTimeMillis();

        if (lastChange > 0 && (currentTime - lastChange) < cooldown && !player.hasPermission("kitpvp.bypass.cooldown")) {
            long remaining = (cooldown - (currentTime - lastChange)) / 1000;
            messageUtil.sendConfigMessage(player, "kit-cooldown", "time", String.valueOf(remaining));
            return;
        }

        plugin.getKitManager().giveKit(player, kitName);
        data.setCurrentKit(kitName);
        data.setLastKitChange(currentTime);

        messageUtil.sendConfigMessage(player, "kit-selected", "kit", kit.getName());
    }

    private void handleListKits(Player player) {
        MessageUtil.sendMessage(player, "&8&m----&r &c&lAvailable Kits &8&m----&r");
        for (Kit kit : plugin.getKitManager().getKits().values()) {
            if (kit.getPermission().isEmpty() || player.hasPermission(kit.getPermission())) {
                MessageUtil.sendMessage(player, kit.getName() + " &7- " + kit.getDescription());
            }
        }
    }

    private void openKitGUI(Player player) {
        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (!data.isInArena()) {
            messageUtil.sendConfigMessage(player, "not-in-arena");
            return;
        }

        int size = ((plugin.getKitManager().getKits().size() + 8) / 9) * 9;
        size = Math.min(54, Math.max(9, size));

        Inventory gui = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', "&c&lSelect a Kit"));

        int slot = 0;
        for (Kit kit : plugin.getKitManager().getKits().values()) {
            if (kit.getPermission().isEmpty() || player.hasPermission(kit.getPermission())) {
                ItemStack icon = new ItemBuilder(kit.getIcon())
                        .setName(kit.getName())
                        .addLoreLine(kit.getDescription())
                        .addLoreLine("")
                        .addLoreLine(ChatColor.YELLOW + "Click to select!")
                        .build();

                gui.setItem(slot, icon);
                slot++;
            }
        }

        player.openInventory(gui);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("select");
            completions.add("list");
            if (sender.hasPermission("kitpvp.create")) {
                completions.add("create");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("select")) {
            completions.addAll(plugin.getKitManager().getKits().keySet());
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
