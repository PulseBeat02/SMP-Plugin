package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.PlayerStatus;
import com.github.pulsebeat02.SMPPlugin;
import com.github.pulsebeat02.command.GlobalTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Map;
import java.util.UUID;

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
        plugin.getDeathMessages().remove(pl.getUniqueId());
        Map<UUID, PlayerStatus> status = plugin.getStatus();
        status.get(pl.getUniqueId()).setWar(true);
        status.get(pl.getUniqueId()).setWarCooldown(GlobalTime.WAR_TO_PEACEFUL.getTime());
        pl.setDisplayName(pl.getName() + ChatColor.BOLD + " " + ChatColor.RED + "[War]");
        pl.sendMessage(plugin.formatMessage(ChatColor.RED + "Your status was changed to war due to being in the End. You cannot change it back until you leave"));
    }

}
