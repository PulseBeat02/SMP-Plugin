package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PlayerServerLeaveListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerServerLeaveListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Map.Entry<CommandSender, CommandSender> msg : plugin.getMessages().entrySet()) {
            if (msg.getKey().equals(player) || msg.getValue().equals(player)) {
                plugin.getMessages().remove(msg.getKey());
            }
        }
    }

}
