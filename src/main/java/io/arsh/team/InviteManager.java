package io.arsh.team;

import io.arsh.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class InviteManager {

    private final JavaPlugin plugin;
    private final TeamManager teamManager;
    private final Map<Player, InviteData> invitions;
    private final String PREFIX = "&2&lS&b&lM&f&lP ";

    public InviteManager(JavaPlugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.invitions = new HashMap<>();
    }

    public void sendInvite(Player inviter, Player receiver) {
        InviteData data = new InviteData(teamManager.getTeamData(inviter), inviter, receiver);
        invitions.put(receiver, data);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                invitions.remove(receiver);
                if (inviter != null) {
                    inviter.sendMessage(Color.colorize(PREFIX + "&fInvitation for &3" + receiver.getDisplayName() + "&f has expired."));
                    inviter.playSound(inviter.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 100, 2);
                }
                if (receiver != null) {
                    receiver.sendMessage(Color.colorize(PREFIX + "&fInvitation of " + data.teamData().getColor() + data.teamData().getSymbol() + " " + data.teamData().getName()  + "&f has expired."));
                    receiver.playSound(inviter.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 100, 2);
                }
        }, 20 * 60 * 10);
    }

    public void acceptInvite(InviteData inviteData) {
        teamManager.addMember(inviteData.teamData.getUUID(), inviteData.receiver);
        invitions.remove(inviteData.receiver());
    }

    public void declineInvite(InviteData inviteData) {
        invitions.remove(inviteData.receiver());
    }

    public boolean hasInvite(Player player) {
        return invitions.containsKey(player);
    }

    public InviteData getInvite(Player player) {
        return invitions.get(player);
    }

    public record InviteData(TeamManager.TeamData teamData, Player inviter, Player receiver) {
    }

}
