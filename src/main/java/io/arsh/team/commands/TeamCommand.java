package io.arsh.team.commands;

import io.arsh.team.InviteManager;
import io.arsh.team.TeamManager;
import io.arsh.team.commands.subCommands.TeamBase;
import io.arsh.team.commands.subCommands.TeamCreate;
import io.arsh.team.commands.subCommands.TeamDisband;
import io.arsh.team.commands.subCommands.TeamSetBase;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeamCommand implements CommandExecutor, TabCompleter {

    private final TeamManager teamManager;
    private final InviteManager inviteManager;
    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final ArrayList<SubCommand> subCommand;
    public ArrayList<SubCommand> getSubCommand() {
        return subCommand;
    }

    public TeamCommand(TeamManager teamManager, InviteManager inviteManager) {
        this.teamManager = teamManager;
        this.inviteManager = inviteManager;
        subCommand = new ArrayList<>();
        subCommand.add(new TeamCreate(teamManager));
        subCommand.add(new TeamDisband(teamManager));
        subCommand.add(new TeamSetBase(teamManager));
        subCommand.add(new TeamBase(teamManager));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                player.sendMessage("");
                player.sendMessage(Color.colorize("&r                       &3&lTEAM COMMANDS"));
                player.sendMessage("");
                player.sendMessage(Color.colorize("&3 /team help &7- &fHelp command&f."));
                player.sendMessage(Color.colorize("&3 /t <message> &7- &fRun to send private team message&f."));
                player.sendMessage(Color.colorize("&3 /base &7- &fRun to teleport to your team's base location&f."));
                for (int i = 0; i < getSubCommand().size(); i++) {
                    player.sendMessage(Color.colorize("&3 " + getSubCommand().get(i).getSyntax() + " &7- &f" + getSubCommand().get(i).getDescription()));
                }
                player.sendMessage("");
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100, 1);
                return true;
            }
            for (int i = 0; i < getSubCommand().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommand().get(i).getName())) {
                    getSubCommand().get(i).perform(player, args);
                    return true;
                }
            }
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command. Use &3/team help&f for help."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return null;
    }

}
