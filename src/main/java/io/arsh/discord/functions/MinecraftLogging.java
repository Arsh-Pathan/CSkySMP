package io.arsh.discord.functions;

import io.arsh.discord.Bot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftLogging implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String message = "> **" + player.getName() + "** just logged into the server.";
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessage(message).queue();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String message = "> **" + player.getName() + "** left the server.";
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessage(message).queue();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        String message = "> " + event.deathMessage();
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessage(message).queue();
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String message = "> **" + player.getName() + "** has made the adveancement [" + event.getAdvancement().getDisplay().title() + "]!";
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessage(message).queue();
    }

}
