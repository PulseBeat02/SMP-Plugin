package com.github.pulsebeat02.listener;

import com.github.pulsebeat02.SMPPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerDeathListener(final SMPPlugin plugin) {
        this.plugin = plugin;
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
            String pName = entity.getName();
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
