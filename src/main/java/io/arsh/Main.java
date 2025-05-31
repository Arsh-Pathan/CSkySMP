package io.arsh;

import io.arsh.Placeholder;
import io.arsh.combat.CombatLog;
import io.arsh.combat.CombatSession;
import io.arsh.lifesteal.HeartManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private HeartManager heartManager;
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);

        this.heartManager = new HeartManager(this);
        heartManager.initialize();

        new CombatLog(this, heartManager);
        new CombatSession(this);
        new Placeholder(heartManager, teamManager).register();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String message = PREFIX + "&3" + player.getName() + "&f joined the game!";
        event.setJoinMessage(Color.colorize(message));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        event.setDeathMessage(Color.colorize("&#F7E7CE💀 " + message));
    }

    @Override
    public void onDisable() {
    }

}
