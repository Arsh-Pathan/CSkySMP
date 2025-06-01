package io.arsh.admin;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import org.bukkit.plugin.java.JavaPlugin;


import java.util.ArrayList;
import java.util.List;

public class AdminManager implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final List<String> adminNames;


    public AdminManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        List<String> loadedNames = config.getStringList("Admins");
        this.adminNames = new ArrayList<>(loadedNames);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(new Arsh(plugin), plugin);
    }

    public void addAdmin(Player player) {
        String playerName = player.getName();
        if (!adminNames.contains(playerName)) {
            adminNames.add(playerName);
            config.set("Admins", adminNames);
            plugin.saveConfig();
        }
    }

    public boolean isAdmin(Player player) {
        return adminNames.contains(player.getName());
    }

    public void removeAdmin(Player player) {
        String playerName = player.getName();
        if (adminNames.remove(playerName)) {
            config.set("Admins", adminNames);
            plugin.saveConfig();
        }
    }

}