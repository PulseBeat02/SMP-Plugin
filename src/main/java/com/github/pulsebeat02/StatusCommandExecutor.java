package com.github.pulsebeat02;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatusCommandExecutor implements CommandExecutor, TabCompleter {

    private final SMPPlugin plugin;

    public StatusCommandExecutor(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        Map<UUID, PlayerStatus> pluginStatus = plugin.getStatus();
        if (sender instanceof ConsoleCommandSender) {
            if (args.length != 3 || !args[0].equalsIgnoreCase("set")) {
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid arguments. /status set [username] [minutes]"));
                return true;
            }
            Player pl = Bukkit.getPlayer(args[1]);
            if (pl == null) {
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Player Username not Found!"));
                return true;
            }
            int minutes;
            try {
                minutes = Integer.parseInt(args[2]);
                plugin.getStatus().get(pl.getUniqueId()).setCombatCooldown(minutes);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Minutes must be an Integer!"));
            }
        } else {
            Player pl = (Player) sender;
            UUID player = pl.getUniqueId();
            PlayerStatus status = pluginStatus.get(player);
            if (args.length == 0) {
                sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Current Status: " + (status.isWar() ? "War" : "Peaceful")));
                return true;
            }
            if (args.length != 2 || !args[0].equalsIgnoreCase("set")) {
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid arguments. /status set [peaceful | war]"));
                return true;
            }
            if (status.isCombat()) {
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You cannot execute this command while combat tagged! (" + status.getCombatCooldown()/1200 + " seconds left)"));
                return true;
            }
            String name = pl.getName();
            switch (args[1]) {
                case "peaceful":
                    if (status.isWar()) {
                        long time = status.getPeacefulCooldown();
                        if (time <= 0) {
                            pluginStatus.get(player).setWar(false);
                            pluginStatus.get(player).setPeacefulCooldown(GlobalTime.PEACEFUL_TO_WAR.getTime());
                            pl.setDisplayName(pl.getDisplayName() + ChatColor.BOLD + " " + ChatColor.AQUA + "[Peaceful]");
                            pl.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Successfully switched to Peaceful Mode"));
                        } else {
                            pl.sendMessage(plugin.formatMessage(ChatColor.RED + "You still have " + (time / 1200 + 1) + " seconds before switching"));
                        }
                    } else {
                        pl.sendMessage(plugin.formatMessage(ChatColor.GOLD + "You are Already in Peaceful Mode!"));
                    }
                    break;
                case "war":
                    if (!status.isWar()) {
                        long time = status.getWarCooldown();
                        if (time <= 0) {
                            pluginStatus.get(player).setWar(true);
                            pluginStatus.get(player).setWarCooldown(GlobalTime.WAR_TO_PEACEFUL.getTime());
                            pl.setDisplayName(pl.getDisplayName() + ChatColor.BOLD + " " + ChatColor.RED + "[War]");
                            pl.sendMessage(plugin.formatMessage(ChatColor.GREEN + "Successfully switched to War Mode"));
                        } else {
                            pl.sendMessage(plugin.formatMessage(ChatColor.RED + "You still have " + (time / 1200 + 1) + " seconds before switching"));
                        }
                    } else {
                        pl.sendMessage(plugin.formatMessage(ChatColor.GOLD + "You are Already in War Mode!"));
                    }
                    break;
                default:
                    pl.sendMessage(plugin.formatMessage(ChatColor.RED + "Bruh that isn't a valid argument"));
                    break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            List<String> complete = new ArrayList<>();
            if (args.length == 1) {
                complete.add("set");
            } else if (args[0].equalsIgnoreCase("set")) {
                complete.add("peaceful");
                complete.add("war");
            }
            return complete;
        }
        return null;
    }
}
