package io.arsh.team.commands;

import io.arsh.team.TeamManager;
import io.arsh.team.events.TeleportHandler;
import io.arsh.utils.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Base implements CommandExecutor {

    private final JavaPlugin plugin;
    private final TeamManager teamManager;

    public Base(JavaPlugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    private final String PREFIX = "&3&lS&b&lM&f&lP ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 0) {
                player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3/base&f."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return true;
            }

            if (!teamManager.hasTeam(player)) {
                player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to this command."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return true;
            }

            if (!teamManager.getTeamData(player).hasBase()) {
                player.sendMessage(Color.colorize(PREFIX + "&fYour team doesn't have a base set yet."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return true;
            }

            Location targetLocation = teamManager.getTeamData(player).getBase();
            TeleportHandler.startTeleport(player, targetLocation);

        }
        return true;
    }
}


