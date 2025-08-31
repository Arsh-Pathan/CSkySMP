package io.arsh.discord.functions;

import io.arsh.discord.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class MinecraftLogging implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String message = "**" + player.getName() + "** join the server.";

        Color embedColor = new Color(144, 238, 144);

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(embedColor)
                .setDescription(message)
                .build();
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessageEmbeds(messageEmbed).queue();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String message = "**" + player.getName() + "** left the server.";

        Color embedColor = new Color(255, 182, 193);

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(embedColor)
                .setDescription(message)
                .build();
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessageEmbeds(messageEmbed).queue();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        String message = ":skull: " + event.getDeathMessage().replace(player.getDisplayName(), "**" + player.getDisplayName() + "**");
        event.setDeathMessage("☠ " + event.getDeathMessage());

        Color embedColor = new Color(105, 105, 105);

        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(embedColor)
                .setDescription(message)
                .build();
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessageEmbeds(messageEmbed).queue();
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement adv = event.getAdvancement();

        if (adv.getDisplay() == null || adv.getDisplay().isHidden()) {
            return;
        }

        String displayName = PlainTextComponentSerializer.plainText()
                .serialize(adv.getDisplay().title());

        Color embedColor = switch (adv.getDisplay().frame()) {
            case GOAL -> new Color(135, 206, 235);
            case TASK -> new Color(144, 238, 144);
            case CHALLENGE -> new Color(221, 160, 221);
        };

        String message = switch (adv.getDisplay().frame()) {
            case GOAL,TASK ->  "**" + player.getName() + "** has made the advancement [" + displayName + "]";
            case CHALLENGE ->  "**" + player.getName() + "** has completed the challenge [" + displayName + "]";
        };
        MessageEmbed messageEmbed = new EmbedBuilder()
                .setColor(embedColor)
                .setDescription(message)
                .build();

        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID)
                .sendMessageEmbeds(messageEmbed)
                .queue();
    }
}
