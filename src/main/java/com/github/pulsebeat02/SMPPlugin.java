package com.github.pulsebeat02;

import com.github.pulsebeat02.command.death.CustomDeathMessageCompleter;
import com.github.pulsebeat02.command.death.CustomDeathMessageExecutor;
import com.github.pulsebeat02.command.dynmap.DynmapCommandExecutor;
import com.github.pulsebeat02.command.message.ReplyCommandCompleter;
import com.github.pulsebeat02.command.message.ReplyCommandExecutor;
import com.github.pulsebeat02.command.rules.RulesCommandExecutor;
import com.github.pulsebeat02.command.status.StatusCommandCompleter;
import com.github.pulsebeat02.command.status.StatusCommandExecutor;
import com.github.pulsebeat02.listener.PlayerAttackListener;
import com.github.pulsebeat02.listener.PlayerDeathListener;
import com.github.pulsebeat02.listener.PlayerServerJoinListener;
import com.github.pulsebeat02.listener.WhisperCommandListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class SMPPlugin extends JavaPlugin {

    // true -> war
    // false -> peaceful
    private Map<UUID, PlayerStatus> status;
    private Map<CommandSender, CommandSender> messages;
    private Set<UUID> deathMessages;
    private Logger logger;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        status = new HashMap<>();
        this.messages = new HashMap<>();
        deathMessages = new HashSet<>();
        logger = getLogger();
        logger.info(ChatColor.YELLOW + "SMP Plugin is Loading");
        long before = System.currentTimeMillis();
        loadConfig();
        loadTimers();
        loadListeners();
        loadCommands();
        long after = System.currentTimeMillis();
        logger.info(ChatColor.YELLOW + "SMP Plugin has Loaded (Took " + (after - before) + " Milliseconds)");
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.YELLOW + "SMP Plugin is Shutting Down");
        long before = System.currentTimeMillis();
        writeConfig();
        saveConfig();
        long after = System.currentTimeMillis();
        logger.info(ChatColor.YELLOW + "SMP Plugin has Shut Down (Took " + (after - before) + " Milliseconds)");
    }

    public void loadCommands() {
        getCommand("status").setExecutor(new StatusCommandExecutor(this));
        getCommand("status").setTabCompleter(new StatusCommandCompleter());
        getCommand("customdeathmessages").setExecutor(new CustomDeathMessageExecutor(this));
        getCommand("customdeathmessages").setTabCompleter(new CustomDeathMessageCompleter());
        getCommand("rules").setExecutor(new RulesCommandExecutor(this));
        getCommand("map").setExecutor(new DynmapCommandExecutor(this));
        getCommand("r").setExecutor(new ReplyCommandExecutor(this));
        getCommand("r").setTabCompleter(new ReplyCommandCompleter(this));
    }

    public void loadListeners() {
        getServer().getPluginManager().registerEvents(new PlayerServerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerAttackListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new WhisperCommandListener(this), this);
    }

    public void loadTimers() {
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, this::decrementTimers, 20L, 20L);
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, this::writeConfig, 20L, 20L * 15L * 60L);
    }

    public void decrementTimers() {
        for (PlayerStatus status : status.values()) {
            status.setWarCooldown(status.getWarCooldown() - 20);
            status.setPeacefulCooldown(status.getPeacefulCooldown() - 20);
            status.setCombatCooldown(status.getCombatCooldown() - 20);
            if (status.getCombatCooldown() <= 0) {
                status.setCombat(false);
            }
        }
    }

    public void loadConfig() {
        if (!getDataFolder().exists() || config == null) {
            saveConfig();
        }
        config = getConfig();
        for (String uuid : getConfig().getKeys(false)) {
            boolean atWar = config.getBoolean(uuid + ".Status");
            long peacefulcd = config.getLong(uuid + ".PeacefulCooldown");
            long warcd = config.getLong(uuid + ".WarCooldown");
            boolean combat = config.getBoolean(uuid + ".Combat");
            long combatcd = config.getLong(uuid + ".CombatCooldown");
            if (config.getBoolean(uuid + ".CustomDeathMessages")) {
                deathMessages.add(UUID.fromString(uuid));
            }
            status.put(UUID.fromString(uuid), new PlayerStatus(atWar, peacefulcd, warcd, combat, combatcd));
        }
    }

    public void writeConfig() {
        for (Map.Entry<UUID, PlayerStatus> entry : status.entrySet()) {
            String key = entry.getKey().toString();
            PlayerStatus stat = entry.getValue();
            config.set(key + ".Status", stat.isWar());
            config.set(key + ".PeacefulCooldown", stat.getPeacefulCooldown());
            config.set(key + ".WarCooldown", stat.getWarCooldown());
            config.set(key + ".Combat", stat.isCombat());
            config.set(key + ".CombatCooldown", stat.getCombatCooldown());
            config.set(key + ".CustomDeathMessages", deathMessages.contains(entry.getKey()));
        }
    }

    public String formatMessage(String message) {
        return ChatColor.GOLD + "" + ChatColor.BOLD + "[SMP]" + ChatColor.GOLD + " " + message;
    }

    public String getFormattedSenderMessage(final CommandSender who, final String message) {
        return ChatColor.LIGHT_PURPLE + who.getName()
                + ChatColor.RED + " -> "
                + ChatColor.LIGHT_PURPLE + "me"
                + ChatColor.RED + ": "
                + ChatColor.AQUA + message;
    }

    public Map<UUID, PlayerStatus> getStatus() {
        return status;
    }

    public Map<CommandSender, CommandSender> getMessages() {
        return messages;
    }

    public Set<UUID> getDeathMessages() {
        return deathMessages;
    }

}
