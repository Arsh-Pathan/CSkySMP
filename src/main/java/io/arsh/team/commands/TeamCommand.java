package io.arsh.team.commands;

import io.arsh.team.InviteManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }

}
