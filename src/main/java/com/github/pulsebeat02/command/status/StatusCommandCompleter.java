package com.github.pulsebeat02.command.status;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class StatusCommandCompleter implements TabCompleter {

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
