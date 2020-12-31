package com.github.pulsebeat02.command.death;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class CustomDeathMessageCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (command.getName().equalsIgnoreCase("customdeathmessages")) {
            return Arrays.asList("true", "false");
        }
        return null;
    }

}
