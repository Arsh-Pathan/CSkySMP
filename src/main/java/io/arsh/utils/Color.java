package io.arsh.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Pattern;

public enum Color {

    RESET(ChatColor.RESET),

    BOLD(ChatColor.BOLD),
    ITALIC(ChatColor.ITALIC),
    UNDERLINE(ChatColor.UNDERLINE),
    STRIKETHROUGH(ChatColor.STRIKETHROUGH),
    MAGIC(ChatColor.MAGIC),

    WHITE(ChatColor.WHITE),
    GRAY(ChatColor.GRAY),
    DARK_GRAY(ChatColor.DARK_GRAY),
    BLACK(ChatColor.BLACK),

    RED(ChatColor.RED),
    DARK_RED(ChatColor.DARK_RED),
    AQUA(ChatColor.AQUA),
    DARK_AQUA(ChatColor.DARK_AQUA),
    BLUE(ChatColor.BLUE),
    DARK_BLUE(ChatColor.DARK_BLUE),
    GREEN(ChatColor.GREEN),
    DARK_GREEN(ChatColor.DARK_GREEN),
    YELLOW(ChatColor.YELLOW),
    GOLD(ChatColor.GOLD),
    PINK(ChatColor.LIGHT_PURPLE),
    PURPLE(ChatColor.DARK_PURPLE);

    private final ChatColor color;

    Color(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) return ChatColor.RESET.toString();

        String withHex = HEX_PATTERN.matcher(text).replaceAll(ctx -> {
            String hex = ctx.group(1);
            return ChatColor.of("#" + hex).toString();
        });

        String translated = ChatColor.translateAlternateColorCodes('&', withHex);

        return translated + ChatColor.RESET ;
    }

}
