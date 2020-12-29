package com.github.pulsebeat02;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SMPPlugin extends JavaPlugin {

    // true -> war
    // false -> peaceful
    private Map<UUID, PlayerStatus> status;
    private Logger logger;
    private FileConfiguration config;
    private Team war;
    private Team peaceful;

    @Override
    public void onEnable() {
        status = new HashMap<>();
        logger = getLogger();
        logger.info(ChatColor.YELLOW + "SMP Plugin is Loading");
        long before = System.currentTimeMillis();
        if (!getDataFolder().exists() || config == null) {
            saveConfig();
        }
        config = getConfig();
        loadConfig();
        loadScoreboard();
        loadTimers();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("status").setExecutor(new StatusCommandExecutor(this));
        long after = System.currentTimeMillis();
        logger.info(ChatColor.YELLOW + "SMP Plugin has Loaded (Took " + (after - before) + " Milliseconds)");
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.YELLOW + "SMP Plugin is Shutting Down...");
        writeConfig();
    }

    public boolean containsPlayer(final UUID player) { return status.containsKey(player); }

    public Map<UUID, PlayerStatus> getStatus() { return status; }

    public Team getWar() {
        return war;
    }

    public Team getPeaceful() {
        return peaceful;
    }

    public void loadScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        war = board.registerNewTeam("war");
        war.setSuffix(ChatColor.RED + "[War]");
        peaceful = board.registerNewTeam("peaceful");
        peaceful.setSuffix(ChatColor.AQUA + "[Peaceful]");
    }

    public void loadTimers() {
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            for (PlayerStatus status : status.values()) {
                status.setWarCooldown(status.getWarCooldown() - 20);
                status.setPeacefulCooldown(status.getPeacefulCooldown() - 20);
                status.setCombatCooldown(status.getCombatCooldown() - 20);
                if (status.getCombatCooldown() <= 0) {
                    status.setCombat(false);
                }
            }
        }, 20L,0L);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::writeConfig, 20L * 15L * 60L, 10L);
    }

    public void loadConfig() {
        for (String uuid : getConfig().getKeys(true)) {
            boolean atWar = config.getBoolean(uuid + ".status");
            long peacefulcd = config.getLong(uuid + ".peacefulCooldown");
            long warcd = config.getLong(uuid + ".warCooldown");
            status.put(UUID.fromString(uuid), new PlayerStatus(atWar, peacefulcd, warcd));
        }
    }

    public void writeConfig() {
        for (Map.Entry<UUID, PlayerStatus> entry : status.entrySet()) {
            String key = entry.getKey().toString();
            PlayerStatus stat = entry.getValue();
            config.set(key + ".status", stat.isWar());
            config.set(key + ".peacefulCooldown", stat.getPeacefulCooldown());
            config.set(key + ".warCooldown", stat.getWarCooldown());
        }
    }

    public String formatMessage(String message) {
        return ChatColor.GOLD + "" + ChatColor.BOLD + "[SMP]" + ChatColor.GOLD + " " + message;
    }

}
