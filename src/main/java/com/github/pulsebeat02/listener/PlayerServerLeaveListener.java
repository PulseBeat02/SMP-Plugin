package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerServerLeaveListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerServerLeaveListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getMessages().entrySet().removeIf(entry -> entry.getKey().equals(player) || entry.getValue().equals(player));
    }

}
