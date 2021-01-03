package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class OnePlayerSleepListener implements Listener {

    private final SMPPlugin plugin;

    public OnePlayerSleepListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

//    @EventHandler
//    public void onPlayerBedEnter(final PlayerBedEnterEvent event) {
//        Player sleeping = event.getPlayer();
//        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
//            if (sleeping.isSleeping()) {
//                for (Player p : Bukkit.getOnlinePlayers()) {
//                    p.sendMessage(plugin.formatMessage(ChatColor.AQUA + "Wakey wakey rise shine, " + sleeping.getName() + " slept"));
//                }
//            }
//        }, 100L);
//    }

}
