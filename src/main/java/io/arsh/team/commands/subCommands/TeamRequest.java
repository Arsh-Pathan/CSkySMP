package io.arsh.team.commands.subCommands;

import io.arsh.team.InviteManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamRequest extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;
    private final InviteManager inviteManager;

    public TeamRequest(TeamManager teamManager, InviteManager inviteManager) {
        this.teamManager = teamManager;
        this.inviteManager = inviteManager;
    }

    @Override
    public String getName() {
        return "request";
    }

    @Override
    public String getDescription() {
        return "Run to accept or deny team invitations.";
    }

    @Override
    public String getSyntax() {
        return "/team request <accept/deny>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = ((Player) sender);
        if (args.length != 2) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3" + getSyntax() + "&f."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
            return;
        }
        if (args[1].equalsIgnoreCase("accept")) {
            if (!inviteManager.hasInvite(player)) {
                player.sendMessage(Color.colorize(PREFIX + "&fYou don't have an invitation to join a team."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return;
            }
            if (teamManager.hasTeam(player)) {
                player.sendMessage(Color.colorize(PREFIX + "&fYou already have a team."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return;
            }
            inviteManager.acceptInvite(inviteManager.getInvite(player));
            player.sendMessage(Color.colorize(PREFIX + "&fYou have accepted the team invitation."));
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 100, 1);
            TeamManager.TeamData team = teamManager.getTeamData(player);
            for (OfflinePlayer member : team.getMembers()) {
                Player onlineMember = member.getPlayer();
                if (onlineMember == null || member.equals(player)) continue;
                onlineMember.sendMessage(PREFIX + "&3" + player.getName() + "&f has join your team.");
                onlineMember.playSound(onlineMember.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 100, 1);
            }
            return;
        }
        if (args[1].equalsIgnoreCase("deny")) {
            if (!inviteManager.hasInvite(player)) {
                player.sendMessage(Color.colorize(PREFIX + "&fYou don't have an invitation to join a team."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return;
            }
            inviteManager.declineInvite(inviteManager.getInvite(player));
            player.sendMessage(Color.colorize(PREFIX + "&fYou have denied the team invitation."));
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 1);
            return;
        }
        player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3" + getSyntax() + "&f."));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
    }

}
