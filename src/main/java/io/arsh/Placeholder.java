package io.arsh;

import io.arsh.lifesteal.HeartManager;
import io.arsh.utils.Color;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {
    
    private final HeartManager heartManager;
    public Placeholder(HeartManager heartManager) {
        this.heartManager = heartManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "csky";
    }

    @Override
    public @NotNull String getAuthor() {
        return "arsh";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        String placeholder = "Invalid";
        if (!offlinePlayer.isOnline()) return placeholder;
        Player player = offlinePlayer.getPlayer();
        if (params.equalsIgnoreCase("hearts")) {
            int hearts = heartManager.getHearts(player);
            if (heartManager.hasHaxHearts(player)) {
                placeholder = "&#7986cbMAX";
            } else {
                placeholder = "&#E57373" + hearts + "&#7986cb♥";
            }
            return Color.colorize(placeholder);
        }
        return placeholder;
    }

}
