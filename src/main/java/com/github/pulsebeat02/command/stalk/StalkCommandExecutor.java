package com.github.pulsebeat02.command.stalk;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StalkCommandExecutor implements CommandExecutor {

    private final SMPPlugin plugin;
    private boolean stalk;

    public StalkCommandExecutor(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You must be a Player to execute this command"));
            return true;
        }
        Player pl = (Player) sender;
        if (args.length == 0) {
            String enabled = ChatColor.GREEN + " " + ChatColor.BOLD + "Enabled";
            String disabled = ChatColor.RED + " " + ChatColor.BOLD + "Disabled";
            pl.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Current Status:" + (stalk ? enabled : disabled)));
            return true;
        }
        if (args.length == 1) {
            try {
                if (Boolean.parseBoolean(args[0])) {
                    pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 2));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.hidePlayer(plugin, pl);
                    }
                    pl.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Stalk Mode Activated, get them Haxers :D"));
                    stalk = true;
                    return true;
                } else {
                    pl.removePotionEffect(PotionEffectType.INVISIBILITY);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.showPlayer(plugin, pl);
                    }
                    pl.sendMessage(plugin.formatMessage(ChatColor.GOLD + "Stalk Mode Disabled, got anyone? D:"));
                    stalk = false;
                    return true;
                }
            } catch (NumberFormatException ex) {
                pl.sendMessage(plugin.formatMessage(ChatColor.RED + "Value must be true or false"));
                return true;
            }
        } else {
            pl.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid Arguments: /stalk [true | false]"));
            return true;
        }
    }

}
