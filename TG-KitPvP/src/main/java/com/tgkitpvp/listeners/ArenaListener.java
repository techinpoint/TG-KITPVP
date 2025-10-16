package com.tgkitpvp.listeners;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.Kit;
import com.tgkitpvp.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaListener implements Listener {
    private final TGKitPVP plugin;

    public ArenaListener(TGKitPVP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        if (!plugin.getArenaManager().isPvPAllowed(victim.getLocation()) ||
                !plugin.getArenaManager().isPvPAllowed(attacker.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (data.isInArena() && plugin.getConfig().getBoolean("settings.prevent-item-drop", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (data.isInArena() && plugin.getConfig().getBoolean("settings.prevent-item-pickup", true)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerData data = plugin.getDataManager().getPlayerData(player);

        if (data.isInArena()) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = ChatColor.stripColor(event.getView().getTitle());

        if (title.equals("Select a Kit")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String kitName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            for (Kit kit : plugin.getKitManager().getKits().values()) {
                String cleanKitName = ChatColor.stripColor(kit.getName());
                if (cleanKitName.equalsIgnoreCase(kitName)) {
                    player.closeInventory();
                    player.performCommand("kit select " + kit.getId());
                    break;
                }
            }
        }
    }
}
