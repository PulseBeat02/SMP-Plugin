package com.github.pulsebeat02;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class PlayerModeSelectionGui implements Listener {

    private final SMPPlugin plugin;
    private final UUID who;
    private final Inventory inv;

    public PlayerModeSelectionGui(final SMPPlugin plugin, final UUID who) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        this.who = who;
        this.inv = Bukkit.createInventory(null, 27, "Choose Mode");
        inv.setItem(11, getPeacefulStack());
        inv.setItem(13, getWarStack());
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, getNormalStack());
            }
        }
    }

    private ItemStack getNormalStack() {
        ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "");
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack getPeacefulStack() {
        ItemStack stack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Peaceful");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Choose me if you want to be PEACEFUL!"));
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack getWarStack() {
        ItemStack stack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "War");
        meta.setLore(Arrays.asList(ChatColor.GOLD + "Choose me if you want to FIGHT!", ChatColor.RED + "" + ChatColor.ITALIC + "Blood for the Blood King"));
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    public Inventory getInventory() { return inv; }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();
        UUID uuid = entity.getUniqueId();
        if (uuid != who) {
            return;
        }
        if (event.getClickedInventory().hashCode() != inv.hashCode()) {
            return;
        }
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 13:
                plugin.getStatus().put(uuid, new PlayerStatus( false, GlobalTime.WAR_TO_PEACEFUL.getTime(), GlobalTime.PEACEFUL_TO_WAR.getTime(), false, 0));
                entity.closeInventory();
                entity.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Set your status to " + ChatColor.GREEN + "Peaceful"));
                event.getHandlers().unregister(this);
                break;
            case 15:
                plugin.getStatus().put(uuid, new PlayerStatus( true, GlobalTime.WAR_TO_PEACEFUL.getTime(), GlobalTime.PEACEFUL_TO_WAR.getTime(), false, 0));
                entity.closeInventory();
                entity.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Set your status to " + ChatColor.RED + "War"));
                event.getHandlers().unregister(this);
                break;
        }

    }

}
