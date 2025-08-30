package io.arsh;

import io.arsh.discord.Bot;
import io.arsh.discord.event.MinecraftChatEvent;
import io.arsh.discord.functions.MinecraftLogging;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;
    private static PlayerProtection playerProtection;

    @Override
    public void onEnable() {
        plugin = this;
        new Bot();
        playerProtection = new PlayerProtection(this);
        getServer().getPluginManager().registerEvents(new MinecraftChatEvent(), this);
        getServer().getPluginManager().registerEvents(new MinecraftLogging(), this);
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessage("# Server is black online!").queue();
    }

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        if (playerProtection != null) {
            playerProtection.saveProtectionData();
        }
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessage("# Server is now offline or restarting!").queue();
    }

}
