package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.PlayerModeSelectionGui;
import com.github.pulsebeat02.PlayerStatus;
import com.github.pulsebeat02.SMPPlugin;
import com.github.pulsebeat02.command.GlobalTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerServerJoinListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerServerJoinListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!plugin.getStatus().containsKey(uuid)) {
            plugin.getStatus().put(uuid, new PlayerStatus(false, GlobalTime.WAR_TO_PEACEFUL.getTime(), GlobalTime.PEACEFUL_TO_WAR.getTime(), false, 0));
            player.openInventory(new PlayerModeSelectionGui(plugin, uuid).getInventory());
        }
        if (plugin.getStatus().get(uuid).isWar()) {
            player.setDisplayName(player.getName() + ChatColor.BOLD + " " + ChatColor.RED + "[War]");
        } else {
            player.setDisplayName(player.getName() + ChatColor.BOLD + " " + ChatColor.AQUA + "[Peaceful]");
        }
        player.setSleepingIgnored(true);
    }

}
