package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamTransferLeadership extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamTransferLeadership(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "transferLeadership";
    }

    @Override
    public String getDescription() {
        return "Run to transfer the leadership of the team.";
    }

    @Override
    public String getSyntax() {
        return "/team transferLeadership <member>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = ((Player) sender);
        if (args.length != 2) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3" + getSyntax() + "&f."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!teamManager.hasTeam(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to use this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!teamManager.getTeamData(player).getLeader().equals(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou must be the leader of your team to run this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            player.sendMessage(Color.colorize(PREFIX + "&fPlayer &3" + args[1] + "&f is not online or doesn't exist."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        assert target != null;

        if (!teamManager.hasTeam(target) || !teamManager.getTeamData(player).getMembers().contains(target)) {
            player.sendMessage(Color.colorize(PREFIX + "&fPlayer &3" + args[1] + "&f is not a member of your team."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
            return;
        }

        if (args[1].equalsIgnoreCase(player.getName())) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou are already leader of your team bruh."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        teamManager.setTeamLeader(teamManager.getTeamData(player).getUUID(), target);
        player.sendMessage(Color.colorize(PREFIX + "&fYou have transferred leadership to &3" + target.getName() + "&f."));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 100, 1);
        target.sendMessage(Color.colorize(PREFIX + "&fLeadership of your team has been transferred to you by &3" + player.getName() + "&f."));
        target.playSound(target.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 100, 1);
    }
}
