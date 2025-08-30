package io.arsh.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Color {

    //Standard Colors
    BLACK("#000000"),
    DARK_BLUE("#0000AA"),
    DARK_GREEN("#00AA00"),
    DARK_AQUA("#00AAAA"),
    DARK_RED("#AA0000"),
    DARK_PURPLE("#AA00AA"),
    GOLD("#FFAA00"),
    GRAY("#AAAAAA"),
    DARK_GRAY("#555555"),
    BLUE("#5555FF"),
    GREEN("#55FF55"),
    AQUA("#55FFFF"),
    RED("#FF5555"),
    LIGHT_PURPLE("#FF55FF"),
    YELLOW("#FFFF55"),
    WHITE("#FFFFFF"),
    //Costume Colors
    ALMOND("#EFDECD"),
    APRICOT("#FBCEB1"),
    AQUAMARINE("#7FFFD4"),
    BEIGE("#F5F5DC"),
    BLUSH("#DE5D83"),
    BLUE_BELL("#A2A2D0"),
    BRIGHT_LILAC("#D891EF"),
    BRIGHT_SKY_BLUE("#87CEFA"),
    BUBBLEGUM_PINK("#FFC1CC"),
    CAMBRIDGE_BLUE("#A3C1AD"),
    CANARY_YELLOW("#FFFF99"),
    CANTALOUPE("#FFA07A"),
    CELESTE("#B2FFFF"),
    CHAMPAGNE("#F7E7CE"),
    CHERRY_BLOSSOM_PINK("#FFB7C5"),
    CORAL("#FF7F50"),
    CORNFLOWER_BLUE("#6495ED"),
    COTTON_CANDY("#FFBCD9"),
    CREAM("#FFFDD0"),
    CYAN("#00CED1"),
    DARK_TURQUOISE("#00CED1"),
    DENIM("#1560BD"),
    DESERT_SAND("#EDC9AF"),
    DUSTY_PINK("#DCAE96"),
    EARTH_YELLOW("#E1A95F"),
    FERN_GREEN("#4F7942"),
    FLAMINGO_PINK("#FC8EAC"),
    FLAX("#EEDC82"),
    FOG("#D7DDE8"),
    GRANNY_SMITH_APPLE("#A8E4A0"),
    HONEYDEW("#F0FFF0"),
    ICE_BLUE("#AFEEEE"),
    IVORY("#FFFFF0"),
    JADE("#00A86B"),
    LAVENDER("#E6E6FA"),
    LEMON_CHIFFON("#FFFACD"),
    LIGHT_BLUE("#ADD8E6"),
    LIGHT_CORAL("#F08080"),
    LIGHT_CYAN("#E0FFFF"),
    LIGHT_GOLDENROD("#FAFAD2"),
    LIGHT_GREEN("#90EE90"),
    LIGHT_PEACH("#FFDAB9"),
    LIGHT_PINK("#FFB6C1"),
    LIGHT_SALMON("#FFA07A"),
    LIGHT_SEA_GREEN("#20B2AA"),
    LIGHT_SKY_BLUE("#87CEFA"),
    LIGHT_SLATE_BLUE("#8470FF"),
    LIGHT_STEEL_BLUE("#B0C4DE"),
    LILAC("#C8A2C8"),
    MINT_CREAM("#F5FFFA"),
    MISTY_ROSE("#FFE4E1"),
    MOONSTONE_BLUE("#73A9C2"),
    MOSS_GREEN("#8A9A5B"),
    OCEAN_BLUE("#4F42B5"),
    OLIVE("#B5B35C"),
    PALE_LILAC("#DCD0FF"),
    PEACH("#FFE5B4"),
    PEACH_PUFF("#FFDAB9"),
    PERIWINKLE("#CCCCFF"),
    SKY_BLUE("#87CEEB"),
    VANILLA("#F3E5AB");

    private final String hex;

    Color(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public static List<Color> getColorList() {
        return Arrays.asList(Color.values());
    }

    public static ChatColor getChatColor(Color color) {
        return ChatColor.of(color.getHex());
    }

    public static String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

}
