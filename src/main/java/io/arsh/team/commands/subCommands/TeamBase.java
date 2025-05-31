package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamBase extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamBase(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "base";
    }

    @Override
    public String getDescription() {
        return "Run to teleport to your team base.";
    }

    @Override
    public String getSyntax() {
        return "/team base";
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
            player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!teamManager.getTeamData(player).hasBase()) {
            player.sendMessage(Color.colorize(PREFIX + "&fYour team doesn't have a base set yet."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        Location location = teamManager.getTeamData(player).getBase();
        // ToDo: Delay the teleportation and check if the player is online and haven't moved in the meantime.
        player.teleport(location);
        player.sendMessage(Color.colorize(PREFIX + "&fYou have been teleported to your team base."));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
    }

}
