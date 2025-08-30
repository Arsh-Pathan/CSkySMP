package io.arsh.combat.commands;

import io.arsh.admin.AdminManager;
import io.arsh.combat.CombatSession;
import io.arsh.utils.Color;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SessionCMD implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final CombatSession session;
    private final String PREFIX = "&5&lS&d&lM&f&lP ";
    private final AdminManager adminManager;

    public SessionCMD(JavaPlugin plugin, CombatSession session, AdminManager adminManager) {
        this.plugin = plugin;
        this.session = session;
        this.adminManager = adminManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!adminManager.isAdmin(player)) return true;
            if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                player.sendMessage("");
                player.sendMessage(Color.colorize("&r                 &5&lCOMBAT SESSION COMMANDS"));
                player.sendMessage("");
                player.sendMessage(Color.colorize("&5  /combatsession help &7- &fHelp command&f."));
                player.sendMessage(Color.colorize("&5  /combatsession start &7- &fRun to force-start combat session&f."));
                player.sendMessage(Color.colorize("&5  /combatsession stop &7- &fRun to force-stop combat session&f."));
                player.sendMessage(Color.colorize("&5  /combatsession status &7- &fRun to get status of combat session&f."));
                player.sendMessage("");
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100.0F, 1.0F);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "start":
                    session.start(true);
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 100.0F, 1.0F);
                    break;
                case "stop":
                    session.stop(true);
                    player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100.0F, 1.0F);
                    break;
                case "status":
                    player.sendMessage(Color.colorize(PREFIX + "&5Combat Session&f is currently: " + (session.isActive() ? "&5ACTIVE" : "&5INACTIVE")));
                    if (session.isActive()) {
                        player.sendMessage(Color.colorize("&fTime left: &5" + session.getTimeLeft() + "&f minutes"));
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100.0F, 1.0F);
                    break;
                default:
                    player.sendMessage(Color.colorize(PREFIX + "&fInvalid command. Use &5/combatsession help&f for help."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                    return true;
            }
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
            plugin.getLogger().info("");
            plugin.getLogger().info(Color.colorize("                 COMBAT SESSION COMMANDS"));
            plugin.getLogger().info("");
            plugin.getLogger().info(Color.colorize(" /combatsession help - Help command."));
            plugin.getLogger().info(Color.colorize(" /combatsession start - Run to force-start combat session."));
            plugin.getLogger().info(Color.colorize(" /combatsession stop - Run to force-stop combat session."));
            plugin.getLogger().info(Color.colorize(" /combatsession status - Run to get status of combat session."));
            plugin.getLogger().info("");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start":
                session.start(true);
                plugin.getLogger().info(Color.colorize(PREFIX + "Combat Session has been force-started."));
                break;
            case "stop":
                session.stop(true);
                plugin.getLogger().info(Color.colorize(PREFIX + "Combat Session has been forced stopped."));
                break;
            case "status":
                plugin.getLogger().info(Color.colorize(PREFIX + "Combat Session&f is currently: " + (session.isActive() ? "ACTIVE" : "INACTIVE")));
                if (session.isActive()) {
                    plugin.getLogger().info(Color.colorize("Time left: " + session.getTimeLeft() + " minutes"));
                }
                break;
            default:
                plugin.getLogger().info(Color.colorize(PREFIX + "Invalid command. Use /combatsession help for help."));
                return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("help", "start", "stop", "status");
        }
        return null;
    }

}
