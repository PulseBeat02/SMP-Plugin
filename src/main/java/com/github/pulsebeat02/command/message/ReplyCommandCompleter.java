package com.github.pulsebeat02.command.message;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class ReplyCommandCompleter implements TabCompleter {

    private final SMPPlugin plugin;

    public ReplyCommandCompleter(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (plugin.getMessages().containsKey(sender)) {
            return Collections.singletonList(plugin.getMessages().get(sender).getName());
        }
        return null;
    }

}
