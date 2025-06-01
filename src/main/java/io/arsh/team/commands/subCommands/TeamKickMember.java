package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamKickMember extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamKickMember(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "Run to kick out a member from your team.";
    }

    @Override
    public String getSyntax() {
        return "/team kick <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = ((Player) sender);
        if (args.length != 2) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3" + getSyntax() + "&f."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
            return;
        }
        if (!teamManager.hasTeam(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to use this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
            return;
        }

        TeamManager.TeamData team = teamManager.getTeamData(player);

        if (!team.getLeader().equals(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou must be the leader of your team to run this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
            return;
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            player.sendMessage(Color.colorize(PREFIX + "&fPlayer &3" + args[1] + "&f is not online or doesn't exist."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        assert target != null;

        if (target.getName().equalsIgnoreCase(player.getName())) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou can't kick yourself from your team."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!team.getMembers().contains(target)) {
            player.sendMessage(Color.colorize(PREFIX + "&fProvided player is not a member of your team."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        team.removeMember(target);
        player.sendMessage(Color.colorize(PREFIX + "&fYou have kicked &3" + target.getName() + "&f out of your team."));
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 100, 1);
        for (OfflinePlayer offlineMember : team.getMembers()) {
            if (offlineMember != null && offlineMember.isOnline()) {
                Player member = offlineMember.getPlayer();
                assert member != null;
                if (team.getLeader().equals(member)) continue;
                member.sendMessage(Color.colorize(PREFIX + "&3" + target.getName() + " has been kicked out of your team."));
                member.playSound(member.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 100, 1);
            }
        }
    }

}