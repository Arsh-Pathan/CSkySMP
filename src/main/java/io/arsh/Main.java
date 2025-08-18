package io.arsh;

import io.arsh.discord.Bot;
import io.arsh.discord.event.MinecraftChatEvent;
import io.arsh.discord.functions.MinecraftLogging;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;
        new Bot();
        getServer().getPluginManager().registerEvents(new MinecraftChatEvent(), this);
        getServer().getPluginManager().registerEvents(new MinecraftLogging(), this);
    }

    public static Main getPlugin() {
        return plugin;
    }

}
