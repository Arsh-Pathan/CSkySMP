package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamCreate extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;
    private final List<String> symbols;

    public TeamCreate(TeamManager teamManager, List<String> symbols) {
        this.teamManager = teamManager;
        this.symbols = symbols;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Run to create a team";
    }

    @Override
    public String getSyntax() {
        return "/team create <name> <color> <symbol>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length != 4) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &3" + getSyntax() + "&f."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (teamManager.hasTeam(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou already have a team."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        String name = args[1];
        String color = args[2].toUpperCase();
        String symbol = args[3];

        if (teamManager.teamExist(name)) {
            player.sendMessage(Color.colorize(PREFIX + "&fTeam with name &3" + name + "&f already exist."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!Color.getColorList().contains(Color.valueOf(color))) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid color. Please provide a valid color."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (symbol.length() != 1 && symbols.contains(symbol)) {
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid symbol. Please provide a valid symbol."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        teamManager.createTeam(name, Color.valueOf(color), symbol, player);

        player.sendMessage(Color.colorize(PREFIX + "&fYour team &3" + name + "&f has been created."));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 100, 1);
        for (Player onlinePlayers : player.getWorld().getPlayers() ) {
            if (onlinePlayers == player) continue;
            onlinePlayers.sendMessage(PREFIX + "&3" + player.getName() + "&f has created a new team " + teamManager.getTeamData(player).getColor() + teamManager.getTeamData(player).getSymbol() + " " + teamManager.getTeamData(player).getName() +  "&f.");
            onlinePlayers.playSound(onlinePlayers.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 100, 1);
        }
    }

}
