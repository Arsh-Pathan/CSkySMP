package io.arsh.team.events;

import io.arsh.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class TeleportHandler implements Listener {

    private static JavaPlugin plugin;
    private static final HashMap<UUID, Integer> teleportTasks = new HashMap<>();
    private static final String PREFIX = "&3&lS&b&lM&f&lP ";

    public TeleportHandler(JavaPlugin plugin) {
        TeleportHandler.plugin = plugin;
    }

    public static void startTeleport(Player player, Location targetLocation) {
        Location initialLocation = player.getLocation().clone();
        UUID uuid = player.getUniqueId();

        player.sendMessage(Color.colorize(PREFIX + "&fTeleporting... don't move!"));
        player.playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 100, 1);

        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            teleportTasks.remove(uuid);
            player.teleport(targetLocation);
            player.sendMessage(Color.colorize(PREFIX + "&fTeleported!"));
        }, 60L);

        teleportTasks.put(uuid, taskId);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!teleportTasks.containsKey(uuid)) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
            int taskId = teleportTasks.remove(uuid);
            Bukkit.getScheduler().cancelTask(taskId);
            player.sendMessage(Color.colorize(PREFIX + "&fTeleport &3cancelled&f because you moved!"));
        }
    }
}
