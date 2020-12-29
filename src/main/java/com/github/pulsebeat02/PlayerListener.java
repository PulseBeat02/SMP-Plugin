package com.github.pulsebeat02;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
            plugin.getPeaceful().addEntry(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerAttack(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) {
            return;
        }
        Player who = (Player) event.getDamager();
        Player attacked = (Player) event.getEntity();
        if (event.getDamage() <= 1) {
            return;
        }
        UUID whoUuid = who.getUniqueId();
        UUID attackedUuid = attacked.getUniqueId();
        PlayerStatus whoStatus = plugin.getStatus().get(whoUuid);
        PlayerStatus attackedStatus = plugin.getStatus().get(attackedUuid);
        if (!attackedStatus.isWar()) {
            event.setCancelled(true);
            who.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because they are in Peaceful Mode"));
        } else if (!whoStatus.isWar()) {
            event.setCancelled(true);
            attacked.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because you are in Peaceful Mode!"));
        } else {
            if (!whoStatus.isCombat()) {
                who.sendMessage(plugin.formatMessage(ChatColor.RED + "You are currently Combat Tagged for attacking " + attacked.getName()));
                plugin.getStatus().get(whoUuid).setCombat(true);
                plugin.getStatus().get(whoUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player entity = event.getEntity();
        Player killer = entity.getKiller();
        String kName = null;
        if (killer != null) {
            kName = killer.getDisplayName();
        }
        String pName = entity.getDisplayName();
        switch (entity.getLastDamageCause().getCause()) {
            case DROWNING:
                event.setDeathMessage(ChatColor.AQUA + pName + " drowned in Pepsi");
                break;
            case ENTITY_ATTACK:
            case ENTITY_SWEEP_ATTACK:
                event.setDeathMessage(ChatColor.RED + pName + " was smited into smithereens by " + kName);
                break;
            case PROJECTILE:
                event.setDeathMessage(ChatColor.RED + pName + " was sniped by " + kName);
                break;
            case SUFFOCATION:
                event.setDeathMessage(ChatColor.RED + pName + " died from suffocation lol");
                break;
            case FALL:
                event.setDeathMessage(ChatColor.RED + pName + " fell from the sky to their death xd");
                break;
            case FIRE:
            case FIRE_TICK:
                event.setDeathMessage(ChatColor.RED + pName + " ate too many spicy chicken wings and burnt to a crisp");
                break;
            case LAVA:
                event.setDeathMessage(ChatColor.RED + pName + " fell into some laba");
                break;
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                event.setDeathMessage(ChatColor.RED + pName + " was blown up!!!!!!!!!!!!!!!");
                break;
            case VOID:
                event.setDeathMessage(ChatColor.RED + pName + " committed Sudoku");
                break;
            case SUICIDE:
                event.setDeathMessage(ChatColor.RED + pName + " just simply died for some odd reason");
                break;
            case STARVATION:
                event.setDeathMessage(ChatColor.RED + pName + " died from starving. Give this dude some food cmon");
                break;
            case POISON:
            case WITHER:
                event.setDeathMessage(ChatColor.RED + pName + " died from the coronavirus. YIKES");
                break;
            case MAGIC:
                event.setDeathMessage(ChatColor.RED + pName + " was killed by Harry Potter");
                break;
            case DRAGON_BREATH:
                event.setDeathMessage(ChatColor.RED + pName + " died from the dragon's stanky breath");
                break;
            case FLY_INTO_WALL:
                event.setDeathMessage(ChatColor.RED + pName + " doesn't know how to use an elytra");
                break;
        }

    }

}
