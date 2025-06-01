package io.arsh.team.commands;

import io.arsh.utils.Color;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Base implements CommandExecutor {

    private final String PREFIX = "&3&lS&b&lM&f&lP ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                player.performCommand("/team base");
            }
            player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command. Use &3/team help&f for help."));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100, 1);
        }
        return true;
    }

}
