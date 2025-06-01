package io.arsh.team.commands.subCommands;

import io.arsh.team.TeamManager;
import io.arsh.utils.Color;
import io.arsh.utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamBase extends SubCommand {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";
    private final JavaPlugin plugin;
    private final TeamManager teamManager;

    public TeamBase(JavaPlugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    @Override
    public String getName() {
        return "base";
    }

    @Override
    public String getDescription() {
        return "Run to teleport to your team base.";
    }

    @Override
    public String getSyntax() {
        return "/team base";
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
            player.sendMessage(Color.colorize(PREFIX + "&fYou don't have a team to this command."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        if (!teamManager.getTeamData(player).hasBase()) {
            player.sendMessage(Color.colorize(PREFIX + "&fYour team doesn't have a base set yet."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return;
        }

        Location targetLocation = teamManager.getTeamData(player).getBase();

        Location startLocation = player.getLocation();
        int delayTicks = 20 * 3;
        int checkInterval = 5;

        player.sendMessage(Color.colorize(PREFIX + "&fTeleporting don't move!"));
        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 100, 1);
        final int[] elapsed = {0};

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    Bukkit.getScheduler().cancelTask(thisTaskId[0]);
                    return;
                }

                Location current = player.getLocation();
                if (!current.equals(startLocation)) {
                    player.sendMessage(Color.colorize(PREFIX + "&fTeleportation has cancelled because you moved."));
                    player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 100, 1);
                    Bukkit.getScheduler().cancelTask(thisTaskId[0]);
                    return;
                }

                elapsed[0] += checkInterval;
                if (elapsed[0] >= delayTicks) {
                    player.teleport(targetLocation);
                    player.sendMessage(Color.colorize(PREFIX + "&fYou have been teleported to your team base."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
                    Bukkit.getScheduler().cancelTask(thisTaskId[0]);
                }
            }

            private final int[] thisTaskId = new int[1];

            {
                thisTaskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, checkInterval);
            }

        }, 0L, checkInterval);
    }

}
