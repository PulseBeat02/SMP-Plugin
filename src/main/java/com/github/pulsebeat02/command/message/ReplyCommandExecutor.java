package com.github.pulsebeat02.command.message;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class ReplyCommandExecutor implements CommandExecutor {

    private final SMPPlugin plugin;

    public ReplyCommandExecutor(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (args.length != 2) {
            sender.sendMessage(plugin.formatMessage(ChatColor.RED + "Invalid Arguments: /r [message]"));
            return true;
        }
        Map<CommandSender, CommandSender> messages = plugin.getMessages();
        if (!messages.containsKey(sender)) {
            sender.sendMessage(plugin.formatMessage(ChatColor.RED + "You haven't messaged anyone recently!"));
            return true;
        }
        messages.get(sender).sendMessage(plugin.getFormattedSenderMessage(sender, args[1]));
        return true;
    }


}
