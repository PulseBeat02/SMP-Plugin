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
    private MusicTrackPlayer track;

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
                sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "====================================="));
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Now Playing: " + ChatColor.AQUA + details.title()));
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Author: " + ChatColor.AQUA + details.author()));
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Rating: " + ChatColor.AQUA + details.averageRating()));
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Description: " + ChatColor.AQUA + details.description()));
                sender.sendMessage(plugin.formatMessage(ChatColor.GOLD + "====================================="));
            }
            return true;
        }
        if (!sender.isOp()) {
            plugin.formatMessage(ChatColor.RED + "Tell PulseBeat_02 what songs to play");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {
                track.stopMusic(sender);
                sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Stopping the current track"));
            }
            return true;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("play")) {
                try {
                    track.playMusic(args[1]);
                    sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Playing the current track"));
                } catch (Exception e) {
                    sender.sendMessage(plugin.formatMessage(ChatColor.RED + "An Exception has Occurred"));
                    e.printStackTrace();
                }
            }
            return true;
        }
        return true;
    }

}
