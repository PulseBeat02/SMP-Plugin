package com.github.pulsebeat02.command.death;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomDeathMessageExecutor implements CommandExecutor {

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
        Player player = (Player) sender;
        if (args.length == 0) {
            String enabled = ChatColor.BOLD + " " + ChatColor.GREEN + "Enabled";
            String disabled = ChatColor.BOLD + " " + ChatColor.RED + "Disabled";
            sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Current Custom Death Messages:" + (plugin.getDeathMessages().contains(player.getUniqueId()) ? enabled : disabled)));
        } else if (args.length == 1) {
            try {
                if (Boolean.parseBoolean(args[0])) {
                    plugin.getDeathMessages().add(player.getUniqueId());
                    player.sendMessage(plugin.formatMessage("Allowed Custom Death Messages"));
                } else {
                    plugin.getDeathMessages().remove(player.getUniqueId());
                    player.sendMessage(plugin.formatMessage("Disabled Custom Death Messages"));
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

}
