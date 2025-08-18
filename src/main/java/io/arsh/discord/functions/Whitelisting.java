package io.arsh.discord.functions;

import io.arsh.Main;
import io.arsh.discord.Bot;
import io.arsh.discord.utils.Emoji;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.TimeUnit;

public class Whitelisting {

    public static void handleDiscordChatEvent(MessageReceivedEvent event) {

        if (!event.getChannel().getId().equals(Bot.WHITELIST_CHANNEL_ID)) return;

        User user = event.getAuthor();
        if (user.isBot()) return;

        String username = event.getMessage().getContentRaw().trim();

        if (!username.matches("^[a-zA-Z0-9_]{3,16}$")) {
            event.getMessage().addReaction(Emoji.WRONG).queue();
            event.getMessage()
                    .reply("> **Invalid username!**\n> Please provide a valid Minecraft name. -_-")
                    .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }

        Bukkit.getScheduler().runTask(Main.getPlugin(), () -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(username);

            if (player.isWhitelisted()) {
                event.getMessage().addReaction(Emoji.CORRECT).queue();
                event.getMessage()
                        .reply("> **" + username + "** is already whitelisted on the server! -_-")
                        .queue(msg -> {
                            msg.delete().queueAfter(5, TimeUnit.SECONDS);
                            event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                        });
                return;
            }

            try {
                player.setWhitelisted(true);

                event.getMessage().addReaction(Emoji.CORRECT).queue();
                event.getMessage()
                        .reply("> Successfully whitelisted **" + username + "**! :)\n> You can now join the server 🎉")
                        .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            } catch (Exception ex) {
                event.getMessage().addReaction(Emoji.WRONG).queue();
                event.getMessage()
                        .reply("> Failed to whitelist **" + username + "** :(\n> Please contact Arsh. ")
                        .queue(msg -> msg.delete().queueAfter(5, TimeUnit.SECONDS));
            }
        });
    }

}
