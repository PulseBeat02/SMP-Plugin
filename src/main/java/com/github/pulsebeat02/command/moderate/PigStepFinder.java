package com.github.pulsebeat02.command.moderate;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PigStepFinder implements Listener {

    @EventHandler
    public void onItemMove(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getCursor().getType() == Material.MUSIC_DISC_PIGSTEP) {
            System.out.println(ChatColor.RED + "PLAYER " + event.getWhoClicked().getName() + " CLICKED ON PIGSTEP DISK");
        }
    }

}
