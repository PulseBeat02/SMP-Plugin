package com.github.pulsebeat02.command.rules;

import com.github.pulsebeat02.SMPPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RulesCommandExecutor implements CommandExecutor {

    private final SMPPlugin plugin;

    public RulesCommandExecutor(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        TextComponent message = new TextComponent(plugin.formatMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Click On Me for the Rules of this SMP"));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/PulseBeat02/SMP-Rules/blob/main/RULES.md"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to Visit the Rules")));
        sender.spigot().sendMessage(message);
        return true;
    }

}
