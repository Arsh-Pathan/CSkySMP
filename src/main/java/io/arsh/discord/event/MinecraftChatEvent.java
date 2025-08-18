package io.arsh.discord.event;

import io.arsh.discord.functions.ChatForwarding;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MinecraftChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        ChatForwarding.handleMinecraftChatEvent(event);
    }

}
