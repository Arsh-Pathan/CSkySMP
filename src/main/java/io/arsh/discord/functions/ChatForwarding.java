package io.arsh.discord.functions;

import io.arsh.discord.Bot;
import io.arsh.utils.Color;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatForwarding {

    public static void handleMinecraftChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String format = "**" + player.getName() + "**: " + message;
        event.setFormat(player.getName() + ": " + event.getMessage());
        Bot.guild.getTextChannelById(Bot.CHAT_CHANNEL_ID).sendMessage(format).queue();
    }

    public static void handleDiscordChatEvent(MessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(Bot.CHAT_CHANNEL_ID)) return;

        User user = event.getAuthor();
        if (user.isBot()) return;

        String message = event.getMessage().getContentRaw();

        String format = Color.colorize("&#B1BEE9" + user.getName() + ": &#EDF0FA" + message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(format);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        }
    }

}
