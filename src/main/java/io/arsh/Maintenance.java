package io.arsh;

import io.arsh.utils.Color;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Maintenance implements Listener, CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private final FileConfiguration config;
    private final String PREFIX = "&6&lS&e&lM&f&lP ";

    public Maintenance(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    private boolean isMaintenanceOn() {
        return config.getBoolean("Maintenance.Enable");
    }

    private boolean isWhitelisted(String name) {
        return config.getStringList("Maintenance.Whitelist").contains(name);
    }

    private void whitelistAdd(String name) {
        if (isWhitelisted(name)) return;
        List<String> whitelist = new ArrayList<>(config.getStringList("Maintenance.Whitelist"));
        whitelist.add(name);
        config.set("Maintenance.Whitelist", whitelist);
        plugin.saveConfig();
    }

    private void whitelistRemove(String name) {
        if (!isWhitelisted(name)) return;
        List<String> whitelist = new ArrayList<>(config.getStringList("Maintenance.Whitelist"));
        whitelist.remove(name);
        config.set("Maintenance.Whitelist", whitelist);
        plugin.saveConfig();
    }

    private List<String> getWhitelistPlayers() {
        return config.getStringList("Maintenance.Whitelist");
    }

    private void start(String kickMessage) {
        if (isMaintenanceOn()) return;
        config.set("Maintenance.Enable", true);
        config.set("Maintenance.Kick-Message", kickMessage);
        plugin.saveConfig();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (isWhitelisted(player.getName())) continue;
            player.kick(Component.text(Color.colorize(config.getString("Maintenance.Head-Line") + "\n" + kickMessage)));
        }
    }

    private void stop() {
        if (!isMaintenanceOn()) return;
        config.set("Maintenance.Enable", false);
        plugin.saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!isMaintenanceOn()) return;
        if (isWhitelisted(player.getName())) return;
        player.kick(Component.text(Color.colorize(config.getString("Maintenance.Head-Line") + "\n" + config.getString("Maintenance.Kick-Message"))));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                player.sendMessage("");
                player.sendMessage(Color.colorize( "&r                        &6&lMAINTENANCE COMMANDS"));
                player.sendMessage("");
                player.sendMessage(Color.colorize( "&6  /maintenance help &7- &fHelp command&f."));
                player.sendMessage(Color.colorize( "&6  /maintenance start <kick-message> &7- &fRun to start maintenance&f."));
                player.sendMessage(Color.colorize( "&6  /maintenance stop &7- &fRun to stop maintenance&f."));
                player.sendMessage(Color.colorize( "&6  /maintenance whitelist <add/remove> <player> &7- &fRun to add or remove players from whitelist&f."));
                player.sendMessage("");
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 100.0F, 1.0F);
                return true;
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (args.length == 1) {
                    player.sendMessage(Color.colorize( PREFIX + "&fPlease specify a kick message!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                    return true;
                }
                if (isMaintenanceOn()) {
                    player.sendMessage(Color.colorize( PREFIX + "&fMaintenance mode is already enabled."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                    return true;
                }
                StringBuilder reason = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }
                start(String.join(" ", reason));
                player.sendMessage(Color.colorize( PREFIX + "&fMaintenance mode has been enabled!"));
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 100.0F, 1.0F);
                return true;
            }
            if (args[0].equalsIgnoreCase("stop")) {
                if (!isMaintenanceOn()) {
                    player.sendMessage(Color.colorize( PREFIX + "&fMaintenance mode is already disabled."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                    return true;
                }
                stop();
                player.sendMessage(Color.colorize( PREFIX + "&fMaintenance mode has been disabled!"));
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 100.0F, 1.0F);
                return true;
            }
            if (args[0].equalsIgnoreCase("whitelist")) {
                if (args.length == 1) {
                    player.sendMessage(Color.colorize( PREFIX + "&fInvalid command. Use &6/maintenance whitelist <add/remove> <player>&f."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                    return true;
                }
                if (args[1].equalsIgnoreCase("add")) {
                    if (args.length == 2) {
                        player.sendMessage(Color.colorize( PREFIX + "&fPlease specify a player name!"));
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                        return true;
                    }
                    if (args.length == 3) {
                        String playerName = args[2];
                        if (isWhitelisted(playerName)) {
                            player.sendMessage(Color.colorize( PREFIX + "&fPlayer &6" + playerName + " &fis already on maintenance whitelist."));
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                        } else {
                            whitelistAdd(playerName);
                            player.sendMessage(Color.colorize( PREFIX + "&fPlayer &6" + playerName + " &fis now whitelisted in maintenance whitelist."));
                            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 100.0F, 1.0F);
                        }
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length == 2) {
                        player.sendMessage(Color.colorize( PREFIX + "&fPlease specify a player name!"));
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                        return true;
                    }
                    if (args.length == 3) {
                        String playerName = args[2];
                        if (isWhitelisted(playerName)) {
                            whitelistRemove(playerName);
                            player.sendMessage(Color.colorize( PREFIX + "&fPlayer &6" + playerName + " &fis now removed from maintenance whitelist."));
                            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 100.0F, 1.0F);
                            if (isMaintenanceOn()) {
                                Player onlinePlayer = Bukkit.getPlayer(playerName);
                                if (onlinePlayer != null) {
                                    onlinePlayer.kick(Component.text(Color.colorize(config.getString("Maintenance.Head-Line") + "\n" + config.getString("Maintenance.Kick-Message"))));
                                }
                            }
                        } else {
                            player.sendMessage(Color.colorize( PREFIX + "&fPlayer &6" + playerName + " &fis not on maintenance whitelist."));
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                        }
                        return true;
                    }
                    return true;
                }
                return true;
            }
            player.sendMessage(Color.colorize( PREFIX + "&fInvalid command. Use &6/maintenance help&f for help."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
            plugin.getLogger().info(" ");
            plugin.getLogger().info("                         Maintenance Commands");
            plugin.getLogger().info(" ");
            plugin.getLogger().info("  /maintenance help - Help command.");
            plugin.getLogger().info("  /maintenance start <kick-message> - Run to start maintenance.");
            plugin.getLogger().info("  /maintenance stop - Run to stop maintenance.");
            plugin.getLogger().info("  /maintenance whitelist <add/remove> <player> - &fRun to add or remove players from whitelist.");
            plugin.getLogger().info(" ");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (args.length == 1) {
                plugin.getLogger().info("Please specify a kick message!");
                return true;
            }
            if (isMaintenanceOn()) {
                plugin.getLogger().info("Maintenance mode is already enabled!");
                return true;
            }
            StringBuilder reason = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            start(String.join(" ", reason));
            plugin.getLogger().info("Maintenance mode has been started!");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!isMaintenanceOn()) {
                plugin.getLogger().info("Maintenance mode is already disabled!");
                return true;
            }
            stop();
            plugin.getLogger().info("Maintenance mode has been stopped!");
            return true;
        }
        if (args[0].equalsIgnoreCase("whitelist")) {
            if (args.length == 1) {
                plugin.getLogger().info("Invalid command. Use /maintenance whitelist <add/remove> <player>.");
                return true;
            }
            if (args[1].equalsIgnoreCase("add")) {
                if (args.length == 2) {
                    plugin.getLogger().info("Please specify a player name!");
                    return true;
                }
                if (args.length == 3) {
                    String playerName = args[2];
                    if (isWhitelisted(playerName)) {
                        plugin.getLogger().info("Player " + playerName + " is already on maintenance whitelist.");
                    } else {
                        whitelistAdd(playerName);
                        plugin.getLogger().info("Player " + playerName + " is now whitelisted in maintenance whitelist.");
                    }
                    return true;
                }
                return true;
            }
            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length == 2) {
                    plugin.getLogger().info("Please specify a player name!");
                    return true;
                }
                if (args.length == 3) {
                    String playerName = args[2];
                    if (isWhitelisted(playerName)) {
                        whitelistRemove(playerName);
                        plugin.getLogger().info("Player " + playerName + " is now removed from maintenance whitelist.");
                        if (isMaintenanceOn()) {
                            Player onlinePlayer = Bukkit.getPlayer(playerName);
                            if (onlinePlayer != null) {
                                onlinePlayer.kick(Component.text(Color.colorize(config.getString("Maintenance.Head-Line") + "\n" + config.getString("Maintenance.Kick-Message"))));
                            }
                        }
                    } else {
                        plugin.getLogger().info("Player " + playerName + " is not on maintenance whitelist.");
                    }
                    return true;
                }
                return true;
            }
            return true;
        }
        plugin.getLogger().info("Invalid command. Use /maintenance help for help.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("help", "start", "stop", "whitelist");
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("start")) {
            return List.of("<kick-message>");
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("stop")) {
            return null;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            return List.of("add", "remove");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("whitelist")) {
            if (args[1].equalsIgnoreCase("add")) {
                List<String> list = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!isWhitelisted(player.getName())) {
                        list.add(player.getName());
                    }
                }
                return list;
            } else if (args[1].equalsIgnoreCase("remove")) {
                return getWhitelistPlayers();
            }
        }
        return null;
    }

}
