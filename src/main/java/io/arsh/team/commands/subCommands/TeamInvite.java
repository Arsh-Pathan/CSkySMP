package io.arsh.team.commands.subCommands;

import io.arsh.team.InviteManager;
import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamInvite extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final TeamManager teamManager;
    private final InviteManager inviteManager;

    public TeamInvite(TeamManager teamManager, InviteManager inviteManager) {
        this.teamManager = teamManager;
        this.inviteManager = inviteManager;
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Run to invite players to your team.";
    }

    @Override
    public String getSyntax() {
        return "/team invite <player>";
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

        TeamManager.TeamData team = teamManager.getTeamData(player);

        if (!team.getLeader().equals(player)) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou must be the leader of your team to run this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            player.sendMessage(Color.colorize(PREFIX + "&fPlayer &3" + args[1] + "&f is not online or doesn't exist."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        assert target != null;

        if (target.getName().equalsIgnoreCase(player.getName())) {
            player.sendMessage(Color.colorize(PREFIX + "&fYou can't invite yourself to your team."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (team.getMembers().contains(target)) {
            player.sendMessage(Color.colorize(PREFIX + "&fProvided player is already a member of your team."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (teamManager.hasTeam(target)) {
            player.sendMessage(Color.colorize(PREFIX + "&fProvided player already has a team."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        inviteManager.sendInvite(player, target);
        player.sendMessage(Color.colorize(PREFIX + "&fYou have invited &3" + target.getName() + "&f to your team."));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 100, 1);
        sendInviteMessage(target, team);
    }

    private void sendInviteMessage(Player player, TeamManager.TeamData team) {
        TextComponent accept = new TextComponent(Color.colorize(PREFIX + "&b&lACCEPT"));
        ClickEvent clickEvent1 = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team request accept");
        accept.setClickEvent(clickEvent1);

        TextComponent middle = new TextComponent(Color.colorize("&r  &3|  "));

        TextComponent deny = new TextComponent(Color.colorize("&b&lDENY"));
        ClickEvent clickEvent2 = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team request deny");
        deny.setClickEvent(clickEvent2);

        player.sendMessage(Color.colorize(PREFIX + "&fYou have been invited to join " + team.getColor() + team.getSymbol() + " " + team.getName() + "&f by &3" + player.getName() + "&f."));
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 100, 1);
        player.spigot().sendMessage(accept, middle, deny);

        }

}
