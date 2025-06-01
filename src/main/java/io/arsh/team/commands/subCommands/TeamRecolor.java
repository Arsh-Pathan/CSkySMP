package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.util.UUID;

public class TeamRecolor extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamRecolor(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "recolor";
    }

    @Override
    public String getDescription() {
        return "Run to re-color your team.";
    }

    @Override
    public String getSyntax() {
        return "/team recolor <color>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 3) {
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

        String name = args[2].toUpperCase();
        Color color = Color.valueOf(args[2]);

        if (!Color.getColorList().contains(Color.valueOf(name))) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid color. Please provide a valid color."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }
        
        UUID uuid = teamManager.getTeamData(player).getUUID();

        teamManager.setTeamColor(uuid, color);
        player.sendMessage(Color.colorize(PREFIX + "&fYour team base has been renamed."));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
    }

}
