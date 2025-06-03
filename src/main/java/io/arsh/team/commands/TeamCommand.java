package io.arsh.team.commands;

import io.arsh.team.InviteManager;
import io.arsh.team.TeamManager;
import io.arsh.team.commands.subCommands.*;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeamCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final TeamManager teamManager;
    private final InviteManager inviteManager;
    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final ArrayList<SubCommand> subCommand;

    public ArrayList<SubCommand> getSubCommand() {
        return subCommand;
    }

    public TeamCommand(JavaPlugin plugin, TeamManager teamManager, InviteManager inviteManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.inviteManager = inviteManager;
        plugin.getCommand("base").setExecutor(new Base(plugin, teamManager));
        plugin.getCommand("t").setExecutor(new TChat(teamManager));
        subCommand = new ArrayList<>();
        subCommand.add(new TeamCreate(teamManager, symbols));
        subCommand.add(new TeamDisband(teamManager));
        subCommand.add(new TeamSetBase(teamManager));
        subCommand.add(new TeamBase(plugin, teamManager));
        subCommand.add(new TeamRemoveBase(teamManager));
        subCommand.add(new TeamChat(teamManager));
        subCommand.add(new TeamTransferLeadership(teamManager));
        subCommand.add(new TeamKickMember(teamManager));
        subCommand.add(new TeamInvite(teamManager, inviteManager));
        subCommand.add(new TeamRequest(teamManager, inviteManager));
        subCommand.add(new TeamInfo(teamManager));
        subCommand.add(new TeamLeave(teamManager));
        subCommand.add(new TeamRename(teamManager));
        subCommand.add(new TeamRecolor(teamManager));
        subCommand.add(new TeamSetSymbol(teamManager, symbols));
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
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = ((Player) sender);
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("?");
            completions.add("help");
            if (teamManager.hasTeam(player)) {
                if (teamManager.getTeamData(player).getLeader().equals(player)) {
                    completions.add("transferleadership");
                    completions.add("kick");
                    completions.add("invite");
                    completions.add("setbase");
                    completions.add("rename");
                    completions.add("recolor");
                    completions.add("setsymbol");
                    if (teamManager.getTeamData(player).hasBase()) {
                        completions.add("removebase");
                    }
                    completions.add("disband");
                } else {
                    completions.add("leave");
                }
                completions.add("chat");
                completions.add("info");
                if (teamManager.getTeamData(player).hasBase()) {
                    completions.add("base");
                }
            } else {
                completions.add("create");
                completions.add("request");
            }
        }

        if (teamManager.hasTeam(player)) {
            TeamManager.TeamData team = teamManager.getTeamData(player);
            if (team.getLeader().equals(player)) {
                if (args[0].equalsIgnoreCase("transferleadership")) {
                    if (args.length == 2) {
                        completions.add("<member>");
                        for (OfflinePlayer member : teamManager.getTeamData(player).getMembers()) {
                            if (member == player) continue;
                            completions.add(member.getName());
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("invite")) {
                    if (args.length == 2) {
                        completions.add("<player>");
                        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                            if (team.getMembers().contains(onlinePlayers)) continue;
                            completions.add(onlinePlayers.getName());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("kick")) {
                    if (args.length == 2) {
                        completions.add("<member>");
                        for (OfflinePlayer offlineMember : team.getMembers()) {
                            if (offlineMember == team.getLeader()) continue;
                            completions.add(offlineMember.getName());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("rename")) {
                    if (args.length == 2) {
                        completions.add("<name>");
                    }
                }
                if (args[0].equalsIgnoreCase("recolor")) {
                    if (args.length == 2) {
                        completions.add("<color>");
                        for (Color color : Color.getColorList()) {
                            completions.add(color.toString());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("setsymbol")) {
                    if (args.length == 2) {
                        completions.add("<symbol>");
                        completions.addAll(symbols);
                    }
                }
            }
            if (args[0].equalsIgnoreCase("chat")) {
                completions.add("<message>");
            }
        } else {
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length == 2) {
                    completions.add("<name>");
                }
                if (args.length == 3) {
                    completions.add("<color>");
                    for (Color color : Color.getColorList()) {
                        completions.add(color.toString());
                    }
                }
                if (args.length == 4) {
                    completions.add("<symbol>");
                    completions.addAll(symbols);
                }
            }
            if (args[0].equalsIgnoreCase("request")) {
                if (args.length == 2) {
                    completions.add("accept");
                    completions.add("deny");
                }
            }
        }
        return completions;
    }


    public List<String> symbols = List.of(
            "⇄", "⌀", "⌂", "⌘", "⌚", "⏏", "⏩", "⏪",
            "⏭", "⏮", "⏯", "⏳", "⏹", "⏺", "⏻", "⏼", "■", "▲",
            "▼", "◆", "◎", "☀", "☁", "☂", "☃", "☄", "★", "☆",
            "☈", "☔", "☠", "☮", "☯", "❤", "✈", "❄", "♠",
            "♥", "♦", "♣", "✉", "✂", "☢", "✖", "✔", "✳", "✴",
            "❇", "©", "®", "㊗", "㊙", "♬", "♯", "☈", "★", "✎",
            "♪", "☽", "♧", "❥", "☻","#", "☏"
    );

}
