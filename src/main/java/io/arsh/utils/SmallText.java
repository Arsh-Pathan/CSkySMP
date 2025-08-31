package io.arsh.utils;

import java.util.Map;

public class SmallText {
    private static final Map<Character, String> SMALL_TEXT_MAP;

    static {
        SMALL_TEXT_MAP = Map.ofEntries(
                Map.entry('a', "ᴀ"),
                Map.entry('b', "ʙ"),
                Map.entry('c', "ᴄ"),
                Map.entry('d', "ᴅ"),
                Map.entry('e', "ᴇ"),
                Map.entry('f', "ғ"),
                Map.entry('g', "ɢ"),
                Map.entry('h', "ʜ"),
                Map.entry('i', "ɪ"),
                Map.entry('j', "ᴊ"),
                Map.entry('k', "ᴋ"),
                Map.entry('l', "ʟ"),
                Map.entry('m', "ᴍ"),
                Map.entry('n', "ɴ"),
                Map.entry('o', "ᴏ"),
                Map.entry('p', "ᴘ"),
                Map.entry('q', "ǫ"),
                Map.entry('r', "ʀ"),
                Map.entry('s', "s"),
                Map.entry('t', "ᴛ"),
                Map.entry('u', "ᴜ"),
                Map.entry('v', "ᴠ"),
                Map.entry('w', "ᴡ"),
                Map.entry('x', "x"),
                Map.entry('y', "ʏ"),
                Map.entry('z', "ᴢ"),
                Map.entry('0', "₀"),
                Map.entry('1', "₁"),
                Map.entry('2', "₂"),
                Map.entry('3', "₃"),
                Map.entry('4', "₄"),
                Map.entry('5', "₅"),
                Map.entry('6', "₆"),
                Map.entry('7', "₇"),
                Map.entry('8', "₈"),
                Map.entry('9', "₉")
        );
    }

    public static String smallizier(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder smallTextBuilder = new StringBuilder();
        for (char c : input.toCharArray()) {
            char lowerC = Character.toLowerCase(c);

            String replacement = SMALL_TEXT_MAP.get(lowerC);
            if (replacement != null) {
                smallTextBuilder.append(replacement);
            } else {
                smallTextBuilder.append(c);
            }
        }

        return smallTextBuilder.toString();
    }

}
