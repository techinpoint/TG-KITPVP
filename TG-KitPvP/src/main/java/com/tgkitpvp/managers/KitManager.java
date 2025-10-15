package com.tgkitpvp.managers;

import com.tgkitpvp.TGKitPVP;
import com.tgkitpvp.models.Kit;
import com.tgkitpvp.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class KitManager {
    private final TGKitPVP plugin;
    private final Map<String, Kit> kits;

    public KitManager(TGKitPVP plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        loadKits();
    }

    public void loadKits() {
        kits.clear();
        ConfigurationSection kitsSection = plugin.getConfig().getConfigurationSection("kits");
        if (kitsSection == null) {
            plugin.getLogger().warning("No kits found in config!");
            return;
        }

        for (String kitId : kitsSection.getKeys(false)) {
            try {
                Kit kit = loadKit(kitId, kitsSection.getConfigurationSection(kitId));
                kits.put(kitId.toLowerCase(), kit);
                plugin.getLogger().info("Loaded kit: " + kitId);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load kit: " + kitId);
                e.printStackTrace();
            }
        }
    }

    private Kit loadKit(String id, ConfigurationSection section) {
        Kit kit = new Kit(id);
        kit.setName(ChatColor.translateAlternateColorCodes('&', section.getString("name", id)));
        kit.setDescription(ChatColor.translateAlternateColorCodes('&', section.getString("description", "")));

        String iconString = section.getString("icon", "IRON_SWORD");
        try {
            kit.setIcon(Material.valueOf(iconString.toUpperCase()));
        } catch (IllegalArgumentException e) {
            kit.setIcon(Material.IRON_SWORD);
            plugin.getLogger().warning("Invalid icon material for kit " + id + ": " + iconString);
        }

        kit.setPermission(section.getString("permission", ""));

        ConfigurationSection itemsSection = section.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String slot : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(slot);
                if (itemSection != null) {
                    ItemStack item = createItemFromConfig(itemSection);
                    if (item != null) {
                        if (slot.equalsIgnoreCase("helmet")) {
                            kit.setHelmet(item);
                        } else if (slot.equalsIgnoreCase("chestplate")) {
                            kit.setChestplate(item);
                        } else if (slot.equalsIgnoreCase("leggings")) {
                            kit.setLeggings(item);
                        } else if (slot.equalsIgnoreCase("boots")) {
                            kit.setBoots(item);
                        } else if (slot.startsWith("slot-")) {
                            try {
                                int slotNumber = Integer.parseInt(slot.substring(5));
                                kit.addItem(slotNumber, item);
                            } catch (NumberFormatException e) {
                                plugin.getLogger().warning("Invalid slot number: " + slot);
                            }
                        }
                    }
                }
            }
        }

        if (section.contains("effects")) {
            for (String effectString : section.getStringList("effects")) {
                try {
                    PotionEffect effect = parsePotionEffect(effectString);
                    if (effect != null) {
                        kit.addEffect(effect);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Invalid effect format: " + effectString);
                }
            }
        }

        return kit;
    }

    private ItemStack createItemFromConfig(ConfigurationSection section) {
        String materialString = section.getString("material");
        if (materialString == null) {
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(materialString.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material: " + materialString);
            return null;
        }

        int amount = section.getInt("amount", 1);
        ItemBuilder builder = new ItemBuilder(material, amount);

        if (section.contains("enchantments")) {
            ConfigurationSection enchantments = section.getConfigurationSection("enchantments");
            if (enchantments != null) {
                for (String enchantName : enchantments.getKeys(false)) {
                    try {
                        Enchantment enchant = Enchantment.getByName(enchantName);
                        if (enchant != null) {
                            int level = enchantments.getInt(enchantName, 1);
                            builder.addEnchantment(enchant, level);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Invalid enchantment: " + enchantName);
                    }
                }
            }
        }

        if (section.contains("potion")) {
            String potionType = section.getString("potion");
            builder.setPotionType(potionType);
        }

        return builder.build();
    }

    private PotionEffect parsePotionEffect(String effectString) {
        String[] parts = effectString.split(":");
        if (parts.length < 3) {
            return null;
        }

        PotionEffectType type = PotionEffectType.getByName(parts[0]);
        if (type == null) {
            return null;
        }

        int amplifier = Integer.parseInt(parts[1]);
        int duration = Integer.parseInt(parts[2]) * 20;

        return new PotionEffect(type, duration, amplifier, false, false);
    }

    public void giveKit(Player player, String kitId) {
        Kit kit = kits.get(kitId.toLowerCase());
        if (kit == null) {
            return;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(kit.getArmor());

        for (Map.Entry<Integer, ItemStack> entry : kit.getItems().entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        for (PotionEffect effect : kit.getEffects()) {
            player.addPotionEffect(effect);
        }

        if (plugin.getConfig().getBoolean("settings.refill-health-on-kit", true)) {
            player.setHealth(player.getMaxHealth());
        }

        if (plugin.getConfig().getBoolean("settings.refill-hunger-on-kit", true)) {
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
    }

    public Kit getKit(String id) {
        return kits.get(id.toLowerCase());
    }

    public Map<String, Kit> getKits() {
        return new HashMap<>(kits);
    }

    public boolean hasKit(String id) {
        return kits.containsKey(id.toLowerCase());
    }
}
