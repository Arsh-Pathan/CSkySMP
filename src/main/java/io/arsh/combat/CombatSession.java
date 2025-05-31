package io.arsh.combat;

import io.arsh.combat.commands.SessionCMD;
import io.arsh.utils.Color;
import org.bukkit.*;
import org.bukkit.boss.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.*;
import java.util.concurrent.TimeUnit;

public class CombatSession implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private boolean isActive = false;
    private BossBar bossBar;
    private long endTime;
    private BukkitRunnable countdownTask;

    private final String PREFIX = "&5&lS&d&lM&f&lP ";

    public CombatSession(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("combatsession").setExecutor(new SessionCMD(plugin, this));

        if (config.getBoolean("Combat-Session.Enable")) {
            this.endTime = config.getLong("Combat-Session.End-Time", 0L);
            if (endTime > System.currentTimeMillis()) {
                this.isActive = true;
                createBossBar();
                startBossBarTimer();
            } else {
                for (World world : Bukkit.getWorlds()) {
                    world.getWorldBorder().reset();
                }
                config.set("Combat-Session.Enable", false);
                config.set("Combat-Session.End-Time", 0L);
                plugin.saveConfig();
            }
        }
        scheduleDailyCombatSession();
    }

    @EventHandler
    public void onPortalUse(PlayerPortalEvent event) {
        if (isActive) {
            Player player = event.getPlayer();
            String message = PREFIX + "Portals are disabled due to an ongoing &5Combat Session&f. Ends in &5" + getTimeLeft() + "&f minutes.";
            player.sendMessage(Color.colorize(message));
            player.playSound(player.getLocation(), Sound.BLOCK_VINE_BREAK, 100.0F, 1.0F);
            event.setCancelled(true);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public int getTimeLeft() {
        long millisLeft = endTime - System.currentTimeMillis();
        return (int) TimeUnit.MILLISECONDS.toMinutes(Math.max(millisLeft, 0));
    }

    public void start(boolean forced) {
        if (isActive) return;

        isActive = true;
        int duration = config.getInt("Combat-Session.Duration", 60);
        endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(duration);

        config.set("Combat-Session.Enable", true);
        config.set("Combat-Session.End-Time", endTime);
        plugin.saveConfig();

        int borderSize = config.getInt("Combat-Session.World-Border", 1024);
        for (World world : Bukkit.getWorlds()) {
            world.getWorldBorder().setSize(borderSize, 60);
        }

        if (forced) {
            Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fA &5Combat Session &fwas force-started by an &5admin&f."));
        } else {
            Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fA &5Combat Session &fhas begun!"));
        }
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F));

        createBossBar();
        startBossBarTimer();
    }

    public void stop(boolean forced) {
        if (!isActive) return;

        isActive = false;
        config.set("Combat-Session.Enable", false);
        config.set("Combat-Session.End-Time", 0L);
        plugin.saveConfig();

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        if (countdownTask != null) {
            countdownTask.cancel();
        }
        if (forced) {
            Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fA &5Combat Session &fwas force-ended by an &5admin&f."));
        } else {
            Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fThe &5Combat Session &fhas ended."));
        }
        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F));
    }


    private void createBossBar() {
        bossBar = Bukkit.createBossBar("Combat Session in Progress", BarColor.RED, BarStyle.SEGMENTED_6, BarFlag.CREATE_FOG);
        bossBar.setVisible(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }
    }

    private void startBossBarTimer() {
        countdownTask = new BukkitRunnable() {
            private boolean warnedOneMinute = false;
            private boolean startedFinalCountdown = false;

            @Override
            public void run() {
                long millisLeft = endTime - System.currentTimeMillis();
                double progress = Math.max(0.0, Math.min(1.0, millisLeft / (double) TimeUnit.MINUTES.toMillis(config.getInt("Combat-Session.Duration"))));

                if (bossBar != null) {
                    bossBar.setProgress(progress);
                    bossBar.setTitle("Session ends in " + getTimeLeft() + " min");
                }

                if (!warnedOneMinute && millisLeft <= TimeUnit.MINUTES.toMillis(1)) {
                    warnedOneMinute = true;
                    Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fCombat session ending in &51 &fminute!"));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
                    }
                }

                if (!startedFinalCountdown && millisLeft <= TimeUnit.SECONDS.toMillis(10)) {
                    startedFinalCountdown = true;
                    new BukkitRunnable() {
                        int secondsLeft = 10;
                        @Override
                        public void run() {
                            if (secondsLeft > 0) {
                                Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fEnding in &5" + secondsLeft + "&f..."));
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F + (10 - secondsLeft) * 0.05F);
                                }
                            } else {
                                cancel(); 
                            }
                            secondsLeft--;
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                }

                if (millisLeft <= 0) {
                    stop(false);
                    cancel();
                }
            }
        };
        countdownTask.runTaskTimer(plugin, 0L, 20L);
    }

    private void scheduleDailyCombatSession() {
        int hour = config.getInt("Combat-Session.Start-At", 17);
        long delay = getInitialDelayToIST(hour);
        long ticksPerDay = 20L * 60 * 60 * 24;

        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    long secondsLeft = 60;
                    @Override
                    public void run() {
                        if (secondsLeft == 60 || secondsLeft == 30 || secondsLeft == 10) {
                            Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fCombat session starting in &5" + secondsLeft + " &fseconds."));
                        } else if (secondsLeft <= 5 && secondsLeft > 0) {
                            Bukkit.broadcastMessage(Color.colorize(PREFIX + "&fStarting in &5" + secondsLeft + " &f..."));
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F + (5 - secondsLeft) * 0.1F);
                            }
                        } else if (secondsLeft <= 0) {
                            start(false);
                            cancel();
                            return;
                        }
                        secondsLeft--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);
            }
        }.runTaskTimer(plugin, delay, ticksPerDay);
    }

    private long getInitialDelayToIST(int hour) {
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime now = LocalDateTime.now(istZone);
        LocalDateTime nextRun = now.withHour(hour).withMinute(0).withSecond(0).withNano(0);
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        long delayMillis = Duration.between(now, nextRun).toMillis();
        return delayMillis / 50L;
    }

}
