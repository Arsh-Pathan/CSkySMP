package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamLeave extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamLeave(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Run to leave your team.";
    }

    @Override
    public String getSyntax() {
        return "/team leave";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3" + getSyntax() + "&f."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!teamManager.hasTeam(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        TeamManager.TeamData team = teamManager.getTeamData(player);
        team.removeMember(player);
        player.sendMessage(Color.colorize(PREFIX + "&fYou have left your team."));
        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 100, 1);
        for (OfflinePlayer member : team.getMembers()) {
            Player onlineMember = member.getPlayer();
            if (onlineMember == null) continue;
            onlineMember.sendMessage(PREFIX + "&3" + player.getName() + "&f has left your team.");
            onlineMember.playSound(onlineMember.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 100, 1);
        }
    }

}


