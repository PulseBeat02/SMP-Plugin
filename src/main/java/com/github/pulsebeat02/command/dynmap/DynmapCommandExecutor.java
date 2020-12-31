package com.github.pulsebeat02.command.dynmap;

import com.github.pulsebeat02.SMPPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DynmapCommandExecutor implements CommandExecutor {

    private final SMPPlugin plugin;

    public DynmapCommandExecutor(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        TextComponent message = new TextComponent(plugin.formatMessage(ChatColor.BOLD + "" + ChatColor.AQUA + "Click On Me for the Link to Dynamic Map"));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://51.161.84.225:8932/#"));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to Visit the Dynamic Map")));
        sender.spigot().sendMessage(message);
        return true;
    }

}
