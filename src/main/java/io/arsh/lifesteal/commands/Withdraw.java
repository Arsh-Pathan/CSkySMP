package io.arsh.lifesteal.commands;

import io.arsh.lifesteal.HeartManager;
import io.arsh.utils.Color;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Withdraw implements CommandExecutor, TabCompleter {

    private final HeartManager heartManager;
    private final String PREFIX = "&4&lS&c&lM&f&lP ";

    public Withdraw(HeartManager heartManager) {
        this.heartManager = heartManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 1) {
                player.sendMessage(Color.colorize(PREFIX + "&fInvalid use of command use &c/withdraw <amount>&f."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return true;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                player.sendMessage(Color.colorize(PREFIX + "&fInvalid amount. Please provide a valid amount."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return true;
            }
            int amount = Integer.parseInt(args[0]);
            if (amount <= 0) {
                player.sendMessage(Color.colorize(PREFIX + "&fInvalid amount. Please provide a valid amount."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return true;
            }
            int hearts = heartManager.getHearts(player);
            if (hearts <= amount) {
                player.sendMessage(Color.colorize(PREFIX + "&fYou can't withdraw your all hearts."));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                return true;
            }
            withdraw(player, amount);
            player.sendMessage(Color.colorize(PREFIX + "&fYou have withdrawn &4" + amount + "&f heart. Now you have &4" + heartManager.getHearts(player) + "&f left."));
            player.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 100.0F, 1.0F);
        }
        return true;
    }

    public void withdraw(Player player, int amount) {
        ItemStack heart = heartManager.getHeartItemStack(amount);
        if (!player.getInventory().isEmpty()) {
            heartManager.dropHeart(player, amount);
        } else {
            heartManager.removeHeart(player, amount);
            player.getInventory().addItem(heart);
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("<amount>");
        }
        return null;
    }

}
