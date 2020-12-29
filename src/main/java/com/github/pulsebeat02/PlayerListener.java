package com.github.pulsebeat02;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!plugin.containsPlayer(uuid)) {
            plugin.getStatus().put(uuid, new PlayerStatus( false, GlobalTime.WAR_TO_PEACEFUL.getTime(), GlobalTime.PEACEFUL_TO_WAR.getTime()));
        }
    }

    @EventHandler
    public void onPlayerAttack(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getDamage() <= 1) {
            return;
        }
        Player who = (Player) event.getDamager();
        Player attacked = (Player) event.getEntity();
        UUID whoUuid = who.getUniqueId();
        UUID attackedUuid = attacked.getUniqueId();
        PlayerStatus whoStatus = plugin.getStatus().get(whoUuid);
        PlayerStatus attackedStatus = plugin.getStatus().get(attackedUuid);
        if (!attackedStatus.isWar()) {
            event.setCancelled(true);
            who.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because they are in Peaceful Mode");
        } else if (!whoStatus.isWar()) {
            event.setCancelled(true);
            attacked.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because you are in Peaceful Mode!");
        } else {
            if (!whoStatus.isCombat()) {
                plugin.getStatus().get(whoUuid).setCombat(true);
                plugin.getStatus().get(whoUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
            }
        }
    }

}
