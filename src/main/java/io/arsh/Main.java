package io.arsh;

import io.arsh.admin.AdminManager;
import io.arsh.combat.CombatLog;
import io.arsh.combat.CombatSession;
import io.arsh.lifesteal.HeartManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

public final class Main extends JavaPlugin implements Listener {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private HeartManager heartManager;
    private TeamManager teamManager;
    private FileConfiguration config;
    private AdminManager adminManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        this.config = getConfig();

        loadServerIcon();
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getPluginManager().registerEvents(new Chat(teamManager), this);

        getCommand("maintenance").setExecutor(new Maintenance(this));
        getServer().getPluginManager().registerEvents(new Maintenance(this), this);

        this.heartManager = new HeartManager(this);
        heartManager.initialize();

        this.teamManager = new TeamManager(this);
        teamManager.initialize();

        this.adminManager = new AdminManager(this);

        new CombatLog(this, heartManager);
        new CombatSession(this, adminManager);
        new Placeholder(heartManager, teamManager).register();



    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String message;
        if (adminManager.isAdmin(player)) {
            message = PREFIX + "&3" + player.getName() + "&f joined the game as an admin!";
            for (Player players : getServer().getOnlinePlayers()) {
                players.playSound(players.getLocation(), Sound.ENTITY_WITHER_SPAWN, 100.0F, 1.0F);
            }
        } else {
            if (teamManager.hasTeam(player)) {
                TeamManager.TeamData team = teamManager.getTeamData(player);
                message = PREFIX + team.getColor() + team.getSymbol() + " " + player.getName() + "&f joined the game!";
            } else {
                message = PREFIX + "&3" + player.getName() + "&f joined the game!";
            }
            for (Player players : getServer().getOnlinePlayers()) {
                players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100.0F, 1.0F);
            }
        }
        event.setJoinMessage(Color.colorize(message));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String message;
        if (teamManager.hasTeam(player)) {
            TeamManager.TeamData team = teamManager.getTeamData(player);
            message = PREFIX + team.getColor() + team.getSymbol() + " " + player.getName() + "&f left the game!";
        } else {
            message = PREFIX + "&3" + player.getName() + "&f left the game!";
        }
        for (Player players : getServer().getOnlinePlayers()) {
            players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100.0F, 1.0F);
        }
        event.setQuitMessage(Color.colorize(message));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        event.setDeathMessage(Color.colorize("&#db0000☠ &f" + message));
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        if (config.getBoolean("Maintenance.Enable")) {
            String firstLine = Color.colorize(config.getString("Maintenance.MOTD.Line1"));
            String secondLine = Color.colorize(config.getString("Maintenance.MOTD.Line2"));
            String motd = firstLine + '\n' + ChatColor.RESET + secondLine;
            event.setMotd(motd);
            File icon = new File(getDataFolder(), config.getString("Maintenance.Icon"));
            CachedServerIcon cachedIcon;
            try {
                cachedIcon = getServer().loadServerIcon(icon);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            event.setServerIcon(cachedIcon);
            return;
        }
        String firstLine = Color.colorize(config.getString("Server.MOTD.Line1"));
        String secondLine = Color.colorize(config.getString("Server.MOTD.Line2"));
        String motd = firstLine + '\n' + ChatColor.RESET + secondLine;
        event.setMotd(motd);
        File icon = new File(getDataFolder(), config.getString("Server.Icon"));
        CachedServerIcon cachedIcon;
        try {
            cachedIcon = getServer().loadServerIcon(icon);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        event.setServerIcon(cachedIcon);
    }

    private void loadServerIcon() {
        List<String> icons = List.of(config.getString("Server.Icon"), config.getString("Maintenance.Icon"));
        for (String icon : icons) {
            InputStream imageStream = getResource(icon);
            File outputFile = new File(getDataFolder(), icon);
            if (outputFile.exists()) return;
            try (OutputStream outputStream = Files.newOutputStream(outputFile.toPath())) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = imageStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (IOException ex) {
                getLogger().severe(ex.getMessage());
            }
        }
    }

}
