package io.arsh.discord;

import io.arsh.discord.event.DiscordChatEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;

public class Bot {

    private final String TOKEN    = "";
    private final String GUILD_ID = "903961968803741746";

    public final static String CHAT_CHANNEL_ID      = "1069286356951502878";
    public final static String WHITELIST_CHANNEL_ID = "1407039696391110668";

    public static JDA bot;
    public static Guild guild;

    public Bot() {
        try {
            bot = JDABuilder.createDefault(TOKEN)
                    .enableIntents(
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_INVITES,
                            GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT
                    )
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();
            Activity activity = Activity.watching("CSky SMP");
            OnlineStatus onlineStatus = OnlineStatus.DO_NOT_DISTURB;
            bot.awaitReady();
            guild = bot.getGuildById(GUILD_ID);
            bot.addEventListener(new DiscordChatEvent());
            Bukkit.getServer().getLogger().info("Discord bot successfully logged in.");
        } catch (Exception ex) {
            Bukkit.getServer().getLogger().severe("Failed to login the discord bot.");
        }
    }

}
