package io.arsh;

import io.arsh.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerProtection implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<UUID, Integer> protectionTimes = new HashMap<>();
    private final Map<UUID, BukkitRunnable> protectionTasks = new HashMap<>();

    public PlayerProtection(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        plugin.saveDefaultConfig();
        loadProtectionData();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void loadProtectionData() {
        protectionTimes.clear();
        if (config.isConfigurationSection("Protected-Players")) {
            for (String key : config.getConfigurationSection("Protected-Players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    int seconds = config.getInt("Protected-Players." + key);
                    protectionTimes.put(uuid, seconds);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveProtectionData() {
        config.set("Protected-Players", null);
        protectionTimes.forEach((uuid, seconds) -> config.set("Protected-Players." + uuid, seconds));
        plugin.saveConfig();
    }

    public void addProtection(Player player, int seconds) {
        UUID uuid = player.getUniqueId();
        cancelProtectionTask(uuid);
        protectionTimes.put(uuid, seconds);
        saveProtectionData();
        startProtectionCountdown(player, seconds);
        player.sendMessage(colorize("Combat protection activated for " + seconds / 60 + " minutes!"));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
    }

    public boolean isProtected(Player player) {
        return protectionTimes.containsKey(player.getUniqueId());
    }

    public String getProtectionTimeMinutes(Player player) {
        return String.valueOf(protectionTimes.getOrDefault(player.getUniqueId(), 0) / 60);
    }

    public List<OfflinePlayer> getProtectedPlayers() {
        List<OfflinePlayer> players = new ArrayList<>();
        protectionTimes.keySet().forEach(uuid -> players.add(Bukkit.getOfflinePlayer(uuid)));
        return players;
    }

    public void removeProtection(Player player) {
        UUID uuid = player.getUniqueId();
        cancelProtectionTask(uuid);
        protectionTimes.remove(uuid);
        saveProtectionData();
    }

    private void startProtectionCountdown(final Player player, final int seconds) {
        final UUID uuid = player.getUniqueId();
        BukkitRunnable task = new BukkitRunnable() {
            int timeLeft = seconds;
            int saveCounter = 0;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    protectionTimes.remove(uuid);
                    protectionTasks.remove(uuid);
                    player.sendMessage(colorize("Your combat protection has expired! You are now vulnerable to attacks."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                    saveProtectionData();
                    cancel();
                    return;
                }

                timeLeft--;
                protectionTimes.put(uuid, timeLeft);

                if (timeLeft % 60 == 0 || timeLeft <= 5) {
                    String message = timeLeft <= 5
                            ? "⚠ Your combat protection ends in " + timeLeft + " seconds!"
                            : "⏳ Protection time left: " + (timeLeft / 60) + " minutes.";
                    player.sendMessage(colorize(message));
                    float pitch = timeLeft <= 5 ? 1.8F : 1.2F;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, pitch);
                }

                saveCounter++;
                if (saveCounter >= 30) {
                    saveProtectionData();
                    saveCounter = 0;
                }
            }
        };
        task.runTaskTimer(plugin, 20L, 20L);
        protectionTasks.put(uuid, task);
    }

    public void onPlayerLogout(Player player) {
        cancelProtectionTask(player.getUniqueId());
    }

    public void onPlayerLogin(Player player) {
        if (!player.hasPlayedBefore()) {
            addProtection(player, 60*30);
        }
        UUID uuid = player.getUniqueId();
        if (protectionTimes.containsKey(uuid)) {
            startProtectionCountdown(player, protectionTimes.get(uuid));
            player.sendMessage(colorize("Welcome back! Your combat protection has resumed."));
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0F, 1.0F);
        }
    }

    private void cancelProtectionTask(UUID uuid) {
        BukkitRunnable task = protectionTasks.remove(uuid);
        if (task != null) task.cancel();
    }

    private String colorize(String message) {
        return Color.colorize(message);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Player damager) {
            if (isProtected(damaged)) {
                event.setCancelled(true);
                damager.sendMessage(colorize("That player is under PvP Protection! You can't harm them right now."));
                damager.playSound(damager.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            } else if (isProtected(damager)) {
                event.setCancelled(true);
                damager.sendMessage(colorize("You are currently under PvP Protection and can't attack others."));
                damager.playSound(damager.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onPlayerLogout(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onPlayerLogin(event.getPlayer());
    }
}
