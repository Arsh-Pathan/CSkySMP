package io.arsh;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class Chat implements Listener {

    private final TeamManager teamManager;

    public Chat(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String format;
        if (teamManager.hasTeam(player)) {
            TeamManager.TeamData team = teamManager.getTeamData(player);
            format = Color.colorize(team.getColor() + team.getSymbol() + " " + player.getName() + "&f: " + event.getMessage());
        } else {
            format = Color.colorize("&f" + player.getName() + "&f: " + event.getMessage());
        }
        event.setFormat(format);

    }

}
