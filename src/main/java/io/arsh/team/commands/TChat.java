package io.arsh.team.commands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TChat implements CommandExecutor, TabCompleter {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;
    public TChat(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!teamManager.hasTeam(player)) {
                player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to use this command."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
                return true;
            }
            if (args.length == 0) {
                player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command. Use &3/t <message>&f."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
                return true;
            }
            String message = String.join(" ", args);
            TeamManager.TeamData team = teamManager.getTeamData(player);
            for (OfflinePlayer offlineMember : team.getMembers()) {
                if (offlineMember != null && offlineMember.isOnline()) {
                    Player member = offlineMember.getPlayer();
                    assert member != null;
                    member.sendMessage(Color.colorize(team.getColor() + "&lT " + team.getColor() + player.getName() + "&f: " + message));
                    member.playSound(member.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
                }
            }
        }
        return true;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = ((Player) sender);
        if (teamManager.hasTeam(player)) {
            return List.of("<message>");
        }
        return null;
    }
}
