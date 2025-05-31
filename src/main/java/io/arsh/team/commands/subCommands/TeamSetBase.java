package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamSetBase extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamSetBase(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "setBase";
    }

    @Override
    public String getDescription() {
        return "Run to set team base at your current location.";
    }

    @Override
    public String getSyntax() {
        return "/team setBase";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
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

        UUID uuid = teamManager.getTeamData(player).getUUID();
        Location location = player.getLocation();

        teamManager.setTeamBase(uuid, location);
        player.sendMessage(Color.colorize(PREFIX + "&fYour team base has been set at your current location."));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 100, 1);
    }

}
