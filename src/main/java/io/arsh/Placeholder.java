package io.arsh;

import io.arsh.lifesteal.HeartManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

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
                return Color.colorize("&c" + hearts);
            }
        } else if (params.equalsIgnoreCase("tps")) {
            double @NotNull [] tps = player.getServer().getTPS();
            double recentTps = tps[0];
            if (recentTps >= 18.0) {
                return "&3&l❇";
            } else if (recentTps >= 12.0) {
                return "&6&l❇";
            } else {
                return "&4&l❇";
            }
        } else if (params.equalsIgnoreCase("team_symbol")) {
            if (teamManager.hasTeam(player)) {
                return Color.colorize(teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol() + " "  + teamManager.getTeamData(player).getColor());
            } else {
                return "&#00d5ff⌘";
            }
        } else if (params.equalsIgnoreCase("team_nametag")) {
            if (teamManager.hasTeam(player)) {
                return Color.colorize(teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol() + " ");
            } else {
                return Color.colorize("&f");
            }
        } else if (params.equalsIgnoreCase("team_tablist")) {
            if (teamManager.hasTeam(player)) {
                return Color.colorize(teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol() + " " + teamManager.getTeamData(player).getColor() + "[&f" +teamManager.getTeamData(player).getName() + teamManager.getTeamData(player).getColor() + "] ");
            } else {
                return "";
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
                if (player.getWorld().hasStorm()) {
                    return Color.colorize("&#77c4da☂");
                }
                long time = player.getWorld().getTime();
                return time >= 12300L && time <= 23850L ? Color.colorize("&b☽") : Color.colorize("&e☀");
            }
            if (environment == World.Environment.NETHER) {
                return Color.colorize("&c🔥");
            }
            if (environment == World.Environment.THE_END) {
                return Color.colorize("&#8c00ff☽");
            }
            return Color.colorize("&f💀");
        } else if (params.equalsIgnoreCase("biome")) {
            String biome = player.getWorld().getBiome(player.getLocation()).toString()
                    .toLowerCase().replace("_", " ");
            World.Environment environment = player.getLocation().getWorld().getEnvironment();
            if (environment == World.Environment.NORMAL) {
                return Color.colorize("&#85ff87" + Arrays.stream(biome.split(" "))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                        .collect(Collectors.joining(" ")));
            }
            if (environment == World.Environment.NETHER) {
                return Color.colorize("&#ff7070" + Arrays.stream(biome.split(" "))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                        .collect(Collectors.joining(" ")));
            }
            if (environment == World.Environment.THE_END) {
                return Color.colorize("&#ff00ff" + Arrays.stream(biome.split(" "))
                        .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                        .collect(Collectors.joining(" ")));
            }
        }
        return null;
    }

}
