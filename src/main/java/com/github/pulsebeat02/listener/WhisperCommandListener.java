package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.SMPPlugin;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Set;

public class WhisperCommandListener implements Listener {

    private static final Set<String> aliases = ImmutableSet.of("tell", "w", "msg");
    private final SMPPlugin plugin;

    public WhisperCommandListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerExecuteCommand(final PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if (!containsAliasPlayer(msg)) {
            return;
        }
        event.setCancelled(true);
        String[] split = msg.split(" ");
        CommandSender from = event.getPlayer();
        CommandSender to = Bukkit.getPlayer(split[1]);
        if (to == null) {
            if (split[1].equalsIgnoreCase("Console") || split[1].equalsIgnoreCase("Server")) {
                to = Bukkit.getConsoleSender();
            } else {
                from.sendMessage(plugin.formatMessage(ChatColor.RED + "That player isn't online or valid!"));
                return;
            }
        }
        String message = plugin.concatenateAfterIndex(split, 2);
        plugin.getMessages().put(from, to);
        plugin.getMessages().put(to, from);
        to.sendMessage(plugin.getFormattedSenderMessageTo(from, message));
        from.sendMessage(plugin.getFormattedSenderMessageFrom(to, message));
    }

    @EventHandler
    public void onConsoleExecuteCommand(final ServerCommandEvent event) {
        String msg = event.getCommand();
        if (!containsAliasConsole(msg)) {
            return;
        }
        event.setCancelled(true);
        String[] split = msg.split(" ");
        CommandSender from = event.getSender();
        CommandSender to = Bukkit.getPlayer(split[1]);
        if (to == null) {
            from.sendMessage(plugin.formatMessage(ChatColor.RED + "That player isn't online or valid!"));
            return;
        }
        String message = plugin.concatenateAfterIndex(split, 2);
        plugin.getMessages().put(from, to);
        plugin.getMessages().put(to, from);
        to.sendMessage(plugin.getFormattedSenderMessageTo(from, message));
        from.sendMessage(plugin.getFormattedSenderMessageFrom(to, message));
    }

    private boolean containsAliasPlayer(final String message) {
        String[] trimmed = message.split(" ");
        for (String alias : aliases) {
            if (("/" + alias).equalsIgnoreCase(trimmed[0])) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAliasConsole(final String message) {
        String[] trimmed = message.split(" ");
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(trimmed[0])) {
                return true;
            }
        }
        return false;
    }

}
