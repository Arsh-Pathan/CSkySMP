package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.team.events.TeleportHandler;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamBase extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final JavaPlugin plugin;
    private final TeamManager teamManager;

    public TeamBase(JavaPlugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
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

        if (!teamManager.getTeamData(player).hasBase()) {
            player.sendMessage(Color.colorize(PREFIX + "&fYour team doesn't have a base set yet."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        Location targetLocation = teamManager.getTeamData(player).getBase();
        TeleportHandler.startTeleport(player, targetLocation);

    }

}
