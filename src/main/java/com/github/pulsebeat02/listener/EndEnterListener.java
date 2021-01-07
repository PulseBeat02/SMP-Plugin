package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class EndEnterListener implements Listener {

    private final SMPPlugin plugin;

    public EndEnterListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        Player pl = event.getPlayer();
        if (!pl.getLocation().getWorld().getName().equals("world_the_end")) {
            return;
        }
        plugin.getStatus().get(pl.getUniqueId()).setWar(true);
        plugin.getDeathMessages().remove(pl.getUniqueId());
        pl.sendMessage(plugin.formatMessage(ChatColor.RED + "Your status was changed to war due to being in the End. You cannot change it back until you leave"));
    }

}
