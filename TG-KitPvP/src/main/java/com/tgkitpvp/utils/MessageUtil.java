package com.tgkitpvp.utils;

import com.tgkitpvp.TGKitPVP;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {
    private final TGKitPVP plugin;

    public MessageUtil(TGKitPVP plugin) {
        this.plugin = plugin;
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public void sendConfigMessage(CommandSender sender, String path) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&c&lTG-KITPVP&8] &r");
        String message = plugin.getConfig().getString("messages." + path, "Message not found: " + path);
        sendMessage(sender, prefix + message);
    }

    public void sendConfigMessage(CommandSender sender, String path, String placeholder, String value) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&c&lTG-KITPVP&8] &r");
        String message = plugin.getConfig().getString("messages." + path, "Message not found: " + path);
        message = message.replace("{" + placeholder + "}", value);
        sendMessage(sender, prefix + message);
    }

    public void sendConfigMessage(CommandSender sender, String path, String[] placeholders, String[] values) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&c&lTG-KITPVP&8] &r");
        String message = plugin.getConfig().getString("messages." + path, "Message not found: " + path);

        for (int i = 0; i < placeholders.length && i < values.length; i++) {
            message = message.replace("{" + placeholders[i] + "}", values[i]);
        }

        sendMessage(sender, prefix + message);
    }

    public static String getPrefix(TGKitPVP plugin) {
        return colorize(plugin.getConfig().getString("messages.prefix", "&8[&c&lTG-KITPVP&8] &r"));
    }
}
