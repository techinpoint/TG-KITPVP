package com.tgkitpvp.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit {
    private final String id;
    private String name;
    private String description;
    private Material icon;
    private String permission;
    private Map<Integer, ItemStack> items;
    private ItemStack[] armor;
    private List<PotionEffect> effects;

    public Kit(String id) {
        this.id = id;
        this.items = new HashMap<>();
        this.armor = new ItemStack[4];
        this.effects = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public void setItems(Map<Integer, ItemStack> items) {
        this.items = items;
    }

    public void addItem(int slot, ItemStack item) {
        this.items.put(slot, item);
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public void setHelmet(ItemStack helmet) {
        this.armor[3] = helmet;
    }

    public void setChestplate(ItemStack chestplate) {
        this.armor[2] = chestplate;
    }

    public void setLeggings(ItemStack leggings) {
        this.armor[1] = leggings;
    }

    public void setBoots(ItemStack boots) {
        this.armor[0] = boots;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<PotionEffect> effects) {
        this.effects = effects;
    }

    public void addEffect(PotionEffect effect) {
        this.effects.add(effect);
    }
}
