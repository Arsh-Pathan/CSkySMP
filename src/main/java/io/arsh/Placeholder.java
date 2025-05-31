package io.arsh;

import io.arsh.lifesteal.HeartManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholder extends PlaceholderExpansion {
    
    private final HeartManager heartManager;
    private final TeamManager teamManager;

    public Placeholder(HeartManager heartManager, TeamManager teamManager) {
        this.heartManager = heartManager;
        this.teamManager = teamManager;
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

    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String placeholder = "&bN/D";
        if (!offlinePlayer.isOnline()) return placeholder;
        Player player = offlinePlayer.getPlayer();

        if (params.equalsIgnoreCase("hearts")) {
            assert player != null;
            int hearts = heartManager.getHearts(player);
            if (heartManager.hasHaxHearts(player)) {
                placeholder = "&4MAX";
            } else {
                placeholder = "&c" + hearts + "&4♥";
            }
        }

        if (params.equalsIgnoreCase("team_nametag") || params.equalsIgnoreCase("team_tablist")) {
            if (teamManager.hasTeam(player)) {
                placeholder = teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol();
            } else {
                placeholder = "";
            }
        }

        if (params.equalsIgnoreCase("team_scoreboard")) {
            if (teamManager.hasTeam(player)) {
                placeholder = teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getName();
            } else {
                placeholder = "&7No Team";
            }
        }

        return Color.colorize(placeholder);
    }

}
