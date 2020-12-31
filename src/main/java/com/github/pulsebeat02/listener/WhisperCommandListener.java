package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.SMPPlugin;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;
import java.util.Set;

public class WhisperCommandListener implements Listener {

    private static final Set<String> aliases = ImmutableSet.of("/tell", "/w", "/msg");
    private final SMPPlugin plugin;

    public WhisperCommandListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerExecuteCommand(final PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if (!containsAlias(msg)) {
            return;
        }
        event.setCancelled(true);
        String[] split = msg.split(" ");
        Player from = event.getPlayer();
        Player to = Bukkit.getPlayer(split[1]);
        if (to == null) {
            from.sendMessage(plugin.formatMessage(ChatColor.RED + "That player isn't online or valid!"));
        }
        String message = split[2];
        Map<CommandSender, CommandSender> messages = plugin.getMessages();
        if (!messages.containsKey(from)) {
            messages.put(from, to);
        }
        to.sendMessage(plugin.getFormattedSenderMessage(from, message));
    }

    private boolean containsAlias(final String message) {
        String trimmed = message.substring(0, 5);
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(trimmed)) {
                return true;
            }
        }
        return false;
    }


}
