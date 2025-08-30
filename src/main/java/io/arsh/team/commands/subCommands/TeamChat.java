package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamChat extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager TeamManager;

    public TeamChat(TeamManager TeamManager) {
        this.TeamManager = TeamManager;
    }

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return "Run to send private team message.";
    }

    @Override
    public String getSyntax() {
        return "/team chat <message>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = ((Player) sender);
        if (args.length < 1) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3" + getSyntax() + "&f."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }
        if (!TeamManager.hasTeam(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to use this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
            return;
        }
        TeamManager.TeamData team = TeamManager.getTeamData(player);
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        for (OfflinePlayer offlineMember : team.getMembers()) {
            if (offlineMember != null && offlineMember.isOnline()) {
                Player member = offlineMember.getPlayer();
                assert member != null;
                member.sendMessage(Color.colorize(team.getColor() + "&lT " + team.getColor() + player.getName() + "&f: " + message));
                member.playSound(member.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);
            }
        }
    }
}
