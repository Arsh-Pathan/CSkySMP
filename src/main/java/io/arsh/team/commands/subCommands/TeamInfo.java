package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class TeamInfo extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;

    public TeamInfo(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Run to get info about your team.";
    }

    @Override
    public String getSyntax() {
        return "/team info";
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

        TeamManager.TeamData team = teamManager.getTeamData(player);

        player.sendMessage("");
        player.sendMessage(Color.colorize("&r                &3&lTEAM INFO"));
        player.sendMessage("");
        player.sendMessage(Color.colorize("&3 Name: &f" + team.getName()));
        player.sendMessage(Color.colorize("&3 Color: &f" + team.getColor() + "■"));
        player.sendMessage(Color.colorize("&3 Symbol: &f" + team.getSymbol()));
        player.sendMessage(Color.colorize("&3 Leader: &f" + team.getLeader().getName()));
        player.sendMessage(Color.colorize("&3 Members &b(&3"+ team.getMembers().size() + "&b)&3: &f"));
        for (OfflinePlayer offlineMember : team.getMembers()) {
            Player member = offlineMember.getPlayer();
            assert member != null;
            player.sendMessage(Color.colorize("   &3- &f" + member.getName()));
        }
        player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);

    }

}
