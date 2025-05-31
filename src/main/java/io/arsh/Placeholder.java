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
                placeholder = "&#7986cbMAX";
            } else {
                placeholder = "&#E57373" + hearts + "&#7986cb♥";
            }
        }

        if (teamManager.hasTeam(player)) {
            if (params.equalsIgnoreCase("team")) {
                placeholder = teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getName();
            }
            if (params.equalsIgnoreCase("team_color_symbol")) {
                placeholder = "&r" + teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol();
            }
            if (params.equalsIgnoreCase("team_name")) {
                placeholder = teamManager.getTeamData(player).getName();
            }
            if (params.equalsIgnoreCase("team_color")) {
                placeholder = "&r" + teamManager.getTeamData(player).getColor();
            }
            if (params.equalsIgnoreCase("team_symbol")) {
                placeholder = teamManager.getTeamData(player).getSymbol();
            }
            if (params.equalsIgnoreCase("team_leader")) {
                placeholder =teamManager.getTeamData(player).getLeader().getName();
            }
        } else {
            if (params.equalsIgnoreCase("team")) {
                placeholder = "&r&bN/D";
            }
        }
        return Color.colorize(placeholder);
    }

}
