package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.PlayerStatus;
import com.github.pulsebeat02.SMPPlugin;
import com.github.pulsebeat02.command.GlobalTime;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerAttackListener implements Listener {

    private static final Set<PotionEffectType> negativePotionEffects = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.SLOW);

    private final SMPPlugin plugin;

    public PlayerAttackListener(final SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAttack(final EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity ent = event.getEntity();
        if (ent instanceof Player) {
            if (damager instanceof Player) {
                Player who = (Player) ent;
                Player attacker = (Player) damager;
                if (event.getDamage() <= 1.5) {
                    return;
                }
                UUID whoUuid = who.getUniqueId();
                UUID attackerUuid = attacker.getUniqueId();
                Map<UUID, PlayerStatus> pluginStatus = plugin.getStatus();
                PlayerStatus whoStatus = pluginStatus.get(whoUuid);
                PlayerStatus attackerStatus = pluginStatus.get(attackerUuid);
                if (!attackerStatus.isWar()) {
                    event.setCancelled(true);
                    attacker.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "Turn off your Peaceful Mode if you want to attack them!"));
                } else if (!whoStatus.isWar()) {
                    event.setCancelled(true);
                    attacker.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because they are in Peaceful Mode"));
                } else {
                    if (!whoStatus.isCombat()) {
                        attacker.sendMessage(plugin.formatMessage(ChatColor.RED + "You are currently Combat Tagged for attacking " + attacker.getName()));
                        pluginStatus.get(attackerUuid).setCombat(true);
                        pluginStatus.get(attackerUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
                    }
                }
            } else if (damager instanceof Arrow) {
                Arrow arr = (Arrow) damager;
                if (!containsBadArrowEffects(arr) && arr.getCustomEffects().size() != 0) {
                    return;
                }
                ProjectileSource source = arr.getShooter();
                if (!(source instanceof Player)) {
                    return;
                }
                Player shooter = (Player) source;
                UUID shooterUuid = shooter.getUniqueId();
                UUID attackedUuid = ent.getUniqueId();
                if (shooterUuid == attackedUuid) {
                    return;
                }
                Map<UUID, PlayerStatus> pluginStatus = plugin.getStatus();
                PlayerStatus shooterStatus = pluginStatus.get(shooterUuid);
                PlayerStatus attackedStatus = pluginStatus.get(attackedUuid);
                if (!attackedStatus.isWar()) {
                    event.setCancelled(true);
                    shooter.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "Turn off your Peaceful Mode if you want to attack them!"));
                } else if (!shooterStatus.isWar()) {
                    event.setCancelled(true);
                    shooter.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because they are in Peaceful Mode"));
                } else {
                    if (!shooterStatus.isCombat()) {
                        shooter.sendMessage(plugin.formatMessage(ChatColor.RED + "You are currently Combat Tagged for shooting " + ent.getName()));
                        pluginStatus.get(shooterUuid).setCombat(true);
                        pluginStatus.get(shooterUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
                    }
                }
                arr.remove();
            } else if (damager instanceof Firework) {
                Firework fw = (Firework) damager;
                ProjectileSource source = fw.getShooter();
                if (!(source instanceof Player)) {
                    return;
                }
                Player shooter = (Player) source;
                UUID shooterUuid = shooter.getUniqueId();
                UUID attackedUuid = ent.getUniqueId();
                if (shooterUuid == attackedUuid) {
                    return;
                }
                Map<UUID, PlayerStatus> pluginStatus = plugin.getStatus();
                PlayerStatus whoStatus = pluginStatus.get(shooterUuid);
                PlayerStatus attackedStatus = pluginStatus.get(attackedUuid);
                if (!attackedStatus.isWar()) {
                    event.setCancelled(true);
                    shooter.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "Turn off your Peaceful Mode if you want to attack them!"));
                } else if (!whoStatus.isWar()) {
                    event.setCancelled(true);
                    shooter.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot attack this player because they are in Peaceful Mode"));
                } else {
                    if (!whoStatus.isCombat()) {
                        shooter.sendMessage(plugin.formatMessage(ChatColor.RED + "You are currently Combat Tagged for shooting " + ent.getName()));
                        pluginStatus.get(shooterUuid).setCombat(true);
                        pluginStatus.get(shooterUuid).setCombatCooldown(GlobalTime.COMBAT_TIMER.getTime());
                    }
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
            playerThrown.sendMessage(plugin.formatMessage(ChatColor.RED + "" + ChatColor.BOLD + "HEY!" + ChatColor.RESET + " " + ChatColor.RED + "You cannot throw potions at some Player(s) because they are in Peaceful Mode!"));
        }
    }

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
