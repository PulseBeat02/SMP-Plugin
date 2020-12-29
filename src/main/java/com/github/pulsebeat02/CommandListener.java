package com.github.pulsebeat02;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandListener implements CommandExecutor, TabCompleter {

    private final SMPPlugin plugin;

    public CommandListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "[SMP] You must be a Player to execute this command!");
            return true;
        }
        Player pl = (Player) sender;
        UUID player = pl.getUniqueId();
        PlayerStatus status = plugin.getStatus().get(player);
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "[SMP] Current Status: " + (status.isWar() ? "War" : "Peaceful"));
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. /status set [peaceful | war]");
            return true;
        }
        if (!args[0].equalsIgnoreCase("set")) {
            sender.sendMessage(ChatColor.RED + "Invalid arguments. /status set [mode]");
            return true;
        }
        if (status.isCombat()) {
            sender.sendMessage(ChatColor.RED + "You cannot execute this command while combat tagged!");
            return true;
        }
        switch (args[1]) {
            case "peaceful":
                if (status.isWar()) {
                    long time = status.getPeacefulCooldown();
                    if (time <= 0) {
                        plugin.getStatus().get(player).setWar(false);
                        plugin.getStatus().get(player).setPeacefulCooldown(GlobalTime.PEACEFUL_TO_WAR.getTime());
                        plugin.getPeaceful().addEntry(pl.getName());
                        pl.sendMessage(ChatColor.GREEN + "Successfully switched to Peaceful Mode");
                    } else {
                        pl.sendMessage(ChatColor.RED + "You still have " + time/1200 + " seconds before switching");
                    }
                } else {
                    pl.sendMessage(ChatColor.GOLD + "You are Already in Peaceful Mode!");
                }
                break;
            case "war":
                if (!status.isWar()) {
                    long time = status.getWarCooldown();
                    if (time <= 0) {
                        plugin.getStatus().get(player).setWar(true);
                        plugin.getStatus().get(player).setWarCooldown(GlobalTime.WAR_TO_PEACEFUL.getTime());
                        plugin.getWar().addEntry(pl.getName());
                        pl.sendMessage(ChatColor.GREEN + "Successfully switched to War Mode");
                    } else {
                        pl.sendMessage(ChatColor.RED + "You still have " + time/1200 + " seconds before switching");
                    }
                } else {
                    pl.sendMessage(ChatColor.GOLD + "You are Already in War Mode!");
                }
                break;
            default:
                pl.sendMessage(ChatColor.RED + "Bruh that isn't a valid argument.");
                break;
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