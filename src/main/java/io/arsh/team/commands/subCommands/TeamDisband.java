package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamDisband extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamDisband(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "disband";
    }

    @Override
    public String getDescription() {
        return "Run to disband your team";
    }

    @Override
    public String getSyntax() {
        return "/team disband";
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
            player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to use this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!teamManager.getTeamData(player).getLeader().equals(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou must be the leader of your team to run this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        UUID uuid = teamManager.getTeamData(player).getUUID();

        player.sendMessage(Color.colorize(PREFIX + "&fYour team &3" + teamManager.getTeamData(player).getName() + "&f has been disbanded."));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100, 1);
        for (Player onlinePlayers : Bukkit.getOnlinePlayers() ) {
            if (onlinePlayers == player) continue;
            onlinePlayers.sendMessage(Color.colorize(PREFIX + "&3" + player.getName() + "&f has disbanded their team " + teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol() + " " + teamManager.getTeamData(player).getName() +  "&f."));
            onlinePlayers.playSound(onlinePlayers.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100, 1);
        }
        teamManager.disbandTeam(uuid);
    }
}
