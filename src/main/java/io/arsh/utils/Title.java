package io.arsh.utils;

import org.bukkit.entity.Player;

public class Title {

    public static void send(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(
                Color.colorize(title),
                Color.colorize(subTitle),
                fadeIn,
                stay,
                fadeOut
        );
    }
}
