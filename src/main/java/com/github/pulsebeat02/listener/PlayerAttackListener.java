package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.PlayerStatus;
import com.github.pulsebeat02.SMPPlugin;
import com.github.pulsebeat02.command.GlobalTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerAttackListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerAttackListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAttack(final EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        boolean player = event.getEntity() instanceof Player;
        if (player && event.getDamager() instanceof Player) {
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
                who.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "Turn off your Peaceful Mode if you want to attack them!"));
            } else {
                if (!whoStatus.isCombat()) {
                    who.sendMessage(plugin.formatMessage(ChatColor.RED + "You are currently Combat Tagged for attacking " + attacked.getName()));
                    pluginStatus.get(whoUuid).setCombat(true);
                    pluginStatus.get(whoUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
                }
            }
        } else if (player) {
            if (!(damager instanceof Arrow)) {
                return;
            }
            Arrow arr = (Arrow) damager;
            if (!containsBadArrowEffects(arr)) {
                return;
            }
            ProjectileSource source = arr.getShooter();
            if (!(source instanceof Player)) {
                return;
            }
            Player who = (Player) source;
            Player attacked = (Player) event.getEntity();
            if (who.getUniqueId() == attacked.getUniqueId()) {
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
                who.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "Turn off your Peaceful Mode if you want to attack them!"));
            } else {
                if (!whoStatus.isCombat()) {
                    who.sendMessage(plugin.formatMessage(ChatColor.RED + "You are currently Combat Tagged for attacking " + attacked.getName()));
                    pluginStatus.get(whoUuid).setCombat(true);
                    pluginStatus.get(whoUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
                }
            }
        }
    }

    @EventHandler
    public void onPotionSplashEvent(final PotionSplashEvent event) {
        ThrownPotion pot = event.getPotion();
        ProjectileSource source = pot.getShooter();
        if (!(source instanceof Player)) {
            return;
        }
        if (!containsBadPotionEffects(pot)) {
            return;
        }
        Player playerThrown = (Player) source;
        Map<UUID, PlayerStatus> pluginStatus = plugin.getStatus();
        boolean flag = false;
        for (LivingEntity ent : event.getAffectedEntities()) {
            if (ent instanceof Player && ent != playerThrown) {
                Player pl = (Player) ent;
                if (!pluginStatus.get(pl.getUniqueId()).isWar()) {
                    event.setIntensity(pl, 0);
                    flag = true;
                } else {
                    if (!pluginStatus.get(playerThrown.getUniqueId()).isWar()) {
                        playerThrown.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "Turn off your Peaceful Mode if you want to use bad potions on players!"));
                        return;
                    }
                }
            }
        }
        if (flag) {
            playerThrown.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot throw potions at some players because they are in Peaceful Mode!"));
        }
    }

    private static Set<PotionEffectType> negativePotionEffects = new HashSet<>(Arrays.asList(PotionEffectType.POISON, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.SLOW));
    private boolean containsBadPotionEffects(final ThrownPotion pot) {
        for (PotionEffect effect : pot.getEffects()) {
            if (negativePotionEffects.contains(effect.getType())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsBadArrowEffects(final Arrow arrow) {
        for (PotionEffectType effect : negativePotionEffects) {
            if (arrow.hasCustomEffect(effect)) {
                return true;
            }
        }
        return false;
    }

}
