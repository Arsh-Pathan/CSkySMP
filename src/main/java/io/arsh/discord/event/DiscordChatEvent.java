package io.arsh.discord.event;

import io.arsh.discord.functions.ChatForwarding;
import io.arsh.discord.functions.Whitelisting;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordChatEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Whitelisting.handleDiscordChatEvent(event);
        ChatForwarding.handleDiscordChatEvent(event);
    }

}
