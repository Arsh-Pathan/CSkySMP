package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TeamSetSymbol extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;
    private final List<String> symbols;

    public TeamSetSymbol(TeamManager teamManager, List<String> symbols) {
        this.teamManager = teamManager;
        this.symbols = symbols;
    }

    @Override
    public String getName() {
        return "setsymbol";
    }

    @Override
    public String getDescription() {
        return "Run to set a new symbol your team.";
    }

    @Override
    public String getSyntax() {
        return "/team setsymbol <symbol>";
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

        String symbol = args[1];

        if (symbol.length() != 1 && symbols.contains(symbol)) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid symbol. Please provide a valid symbol."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        UUID uuid = teamManager.getTeamData(player).getUUID();

        teamManager.setTeamSymbol(uuid, symbol);
        player.sendMessage(Color.colorize(PREFIX + "&fYour team's symbol has been set."));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);
    }

}
