package com.github.pulsebeat02.command.music;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class MusicCommandCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length <= 1) {
            return Arrays.asList("load", "play", "stop", "server");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("server")) {
                return Arrays.asList("start", "stop");
            }
        }
        return null;
    }

}
