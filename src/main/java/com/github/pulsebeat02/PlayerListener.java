package com.github.pulsebeat02;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!plugin.containsPlayer(uuid)) {
            plugin.getStatus().put(uuid, new PlayerStatus( false, GlobalTime.WAR_TO_PEACEFUL.getTime(), GlobalTime.PEACEFUL_TO_WAR.getTime(), false, 0));
            player.setDisplayName(player.getName() + ChatColor.BOLD + " " + ChatColor.AQUA + "[Peaceful]");
        }
    }

    @EventHandler
    public void onPlayerAttack(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) {
            return;
        }
        Player who = (Player) event.getDamager();
        Player attacked = (Player) event.getEntity();
        if (event.getDamage() <= 1.5) {
            return;
        }
        UUID whoUuid = who.getUniqueId();
        UUID attackedUuid = attacked.getUniqueId();
        Map<UUID, PlayerStatus> pluginStatus = plugin.getStatus();
        PlayerStatus whoStatus = pluginStatus.get(whoUuid);
        PlayerStatus attackedStatus = pluginStatus.get(attackedUuid);
        if (!attackedStatus.isWar()) {
            event.setCancelled(true);
            who.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because they are in Peaceful Mode"));
        } else if (!whoStatus.isWar()) {
            event.setCancelled(true);
            attacked.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "They cannot attack you because you are in Peaceful Mode!"));
        } else {
            if (!whoStatus.isCombat()) {
                who.sendMessage(plugin.formatMessage(ChatColor.RED + "You are currently Combat Tagged for attacking " + attacked.getName()));
                pluginStatus.get(whoUuid).setCombat(true);
                pluginStatus.get(whoUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player entity = event.getEntity();
        if (plugin.getDeathMessages().contains(entity.getUniqueId())) {
            Player killer = entity.getKiller();
            String kName = null;
            if (killer != null) {
                kName = killer.getDisplayName();
            } else {
                return;
            }
            String pName = entity.getDisplayName();
            String deathMessage = "";
            switch (entity.getLastDamageCause().getCause()) {
                case DROWNING:
                    deathMessage = ChatColor.AQUA + pName + " drowned in " + ChatColor.BOLD + "Pepsi";
                    break;
                case ENTITY_ATTACK:
                case ENTITY_SWEEP_ATTACK:
                    deathMessage = ChatColor.RED + pName + " was smited into " + ChatColor.BOLD + "smithereens" + ChatColor.RESET + " " + ChatColor.RED + "by " + kName;
                    break;
                case SUFFOCATION:
                    deathMessage = ChatColor.RED + pName + " died from " + ChatColor.BOLD + "suffocation" + ChatColor.RESET + " " + ChatColor.BOLD + "lol";
                    break;
                case FALL:
                    deathMessage = ChatColor.RED + pName + " fell from the sky to their death xd" + ChatColor.MAGIC + " kekw";
                    break;
                case FIRE:
                case FIRE_TICK:
                    deathMessage = ChatColor.RED + pName + " ate too many " + ChatColor.BOLD + "spicy chicken wings" + ChatColor.RESET + ChatColor.RED + " and burnt to a crisp";
                    break;
                case LAVA:
                    deathMessage = ChatColor.RED + pName + " fell into some" + ChatColor.BOLD + " laba";
                    break;
                case BLOCK_EXPLOSION:
                case ENTITY_EXPLOSION:
                    deathMessage = ChatColor.RED + pName + " was blown up!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + ChatColor.AQUA + "!!!!!!!!!!!!!!!";
                    break;
                case VOID:
                    deathMessage = ChatColor.RED + pName + " committed " + ChatColor.BOLD + "Sudoku";
                    break;
                case POISON:
                case WITHER:
                    deathMessage = ChatColor.RED + pName + " died from the " + ChatColor.BOLD + "coronavirus" + ChatColor.RESET + ChatColor.RED + ". YIKES";
                    break;
                case MAGIC:
                    deathMessage = ChatColor.RED + pName + " was killed by " + ChatColor.GOLD + ChatColor.BOLD + "Harry Potter";
                    break;
                case DRAGON_BREATH:
                    deathMessage = ChatColor.RED + pName + " died from the dragon's " + ChatColor.BOLD + "stanky breath";
                    break;
                case FLY_INTO_WALL:
                    deathMessage = ChatColor.RED + pName + " doesn't know how to use an " + ChatColor.BOLD + "elytra";
                    break;
            }
            event.setDeathMessage(deathMessage);
        }
    }

}
