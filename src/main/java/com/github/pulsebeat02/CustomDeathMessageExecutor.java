package com.github.pulsebeat02;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CustomDeathMessageExecutor implements CommandExecutor, TabCompleter {

    private final SMPPlugin plugin;
    public CustomDeathMessageExecutor(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You must be a Player to execute this command."));
            return true;
        }
        if (args.length == 1) {
            Player player = (Player) sender;
            try {
                if (Boolean.parseBoolean(args[0])) {
                    plugin.getDeathMessages().add(player.getUniqueId());
                    player.sendMessage(plugin.formatMessage("Allowed Custom Death Messages"));
                } else {
                    plugin.getDeathMessages().remove(player.getUniqueId());
                    player.sendMessage(plugin.formatMessage("Disallowed Custom Death Messages"));
                }
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.formatMessage("Invalid Value. /customdeathmessages set [true | false]"));
                return true;
            }
        } else {
            sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Wrong Use: /customdeathmessages set [true | false]"));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (command.getName().equalsIgnoreCase("customdeathmessages")) {
            return Arrays.asList("true", "false");
        }
        return null;
    }

}
