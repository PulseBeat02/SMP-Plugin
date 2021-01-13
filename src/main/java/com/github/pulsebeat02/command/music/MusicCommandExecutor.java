package com.github.pulsebeat02.command.music;

import com.github.kiulian.downloader.model.VideoDetails;
import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MusicCommandExecutor implements CommandExecutor {

    private final SMPPlugin plugin;
    private final MusicTrackPlayer track;

    public MusicCommandExecutor(final SMPPlugin plugin) {
        this.plugin = plugin;
        this.track = new MusicTrackPlayer(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.formatMessage(ChatColor.RED + "You must be a player to be the DJ");
            return true;
        }
        if (args.length == 0) {
            VideoDetails details = track.getDetails();
            if (details == null) {
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "No Songs Currently Playing!"));
            } else {
                sender.sendMessage(ChatColor.GOLD + "=====================================");
                sender.sendMessage(ChatColor.AQUA + "Now Playing: " + ChatColor.LIGHT_PURPLE + details.title());
                sender.sendMessage(ChatColor.AQUA + "Author: " + ChatColor.LIGHT_PURPLE + details.author());
                sender.sendMessage(ChatColor.AQUA + "Rating: " + ChatColor.LIGHT_PURPLE + details.averageRating());
                sender.sendMessage(ChatColor.GOLD + "=====================================");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {
                track.stopMusic(sender);
            } else if (args[0].equalsIgnoreCase("play")) {
                if (track.finishedLoading()) {
                    sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Playing Music"));
                    track.playMusic();
                } else {
                    sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Waiting for Audio to Parse!"));
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("load")) {
                try {
                    sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Attempting to Load Track..."));
                    track.loadMusic(sender, args[1]);
                } catch (Exception e) {
                    sender.sendMessage(plugin.formatMessage(ChatColor.RED + "An Exception has Occurred"));
                    e.printStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("server")) {
                boolean alive = plugin.getHTTPServer() == null;
                if (args[1].equalsIgnoreCase("start")) {
                    if (alive) {
                        sender.sendMessage(plugin.formatMessage(ChatColor.RED + "The HTTP Server has Already Started!"));
                    } else {
                        plugin.startHTTPServer();
                        sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Starting the HTTP Server..."));
                    }
                } else if (args[1].equalsIgnoreCase("stop")) {
                    if (!alive) {
                        sender.sendMessage(plugin.formatMessage(ChatColor.RED + "The HTTP Server has Already Stopped!"));
                    } else {
                        plugin.stopHTTPServer();
                        sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Stopping the HTTP Server..."));
                    }
                }
            }
        }
        return true;
    }

}
