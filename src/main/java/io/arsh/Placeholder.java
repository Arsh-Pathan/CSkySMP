package io.arsh;

import io.arsh.lifesteal.HeartManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
        Color.colorize("&bN/D");
        if (!offlinePlayer.isOnline()) return Color.colorize("&bN/D");
        Player player = offlinePlayer.getPlayer();
        assert player != null;

        if (params.equalsIgnoreCase("hearts")) {
            int hearts = heartManager.getHearts(player);
            if (heartManager.hasHaxHearts(player)) {
                return Color.colorize("&4MAX");
            } else {
                return Color.colorize("&c" + hearts + " &4&l♥");
            }
        } else if (params.equalsIgnoreCase("team_nametag") || params.equalsIgnoreCase("team_tablist")) {
            if (teamManager.hasTeam(player)) {
                return Color.colorize(teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol());
            } else {
                return null;
            }
        } else if (params.equalsIgnoreCase("team_scoreboard")) {
            if (teamManager.hasTeam(player)) {
                return Color.colorize(teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getName());
            } else {
                return Color.colorize("&7No Team");
            }
        } else if (params.equalsIgnoreCase("emoji")) {
            World.Environment environment = player.getLocation().getWorld().getEnvironment();
            if (environment == World.Environment.NORMAL) {
                long time = player.getWorld().getTime();
                return time >= 12300L && time <= 23850L ? Color.colorize("&b☽") : Color.colorize("&e☀");
            }
            if (environment == World.Environment.NETHER) {
                return Color.colorize("&c🔥");
            }
            if (environment == World.Environment.THE_END) {
                return Color.colorize("&d☽");
            }
            return Color.colorize("&f💀");
        }
        return null;
    }

}
