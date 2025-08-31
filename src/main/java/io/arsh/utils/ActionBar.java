package io.arsh.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBar {

    public static void send(Player player, String message) {
        if (player == null || message == null) return;
        String colored = Color.colorize(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colored));
    }

}
