package io.arsh.lifesteal.events;

import io.arsh.lifesteal.HeartManager;
import io.arsh.utils.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HeartClick implements Listener {

    private final HeartManager heartManager;
    private final String PREFIX = "&4&lS&c&lM&f&lP ";

    public HeartClick(HeartManager heartManager) {
        this.heartManager = heartManager;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        Player player = event.getPlayer();
        if (action.isRightClick()) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getItemMeta() == null) return;
            if (itemInHand.getItemMeta().equals(heartManager.getHeartItemStack(1).getItemMeta())) {
                if (heartManager.hasHaxHearts(player)) {
                    player.sendMessage(Color.colorize( PREFIX + "&fYou have reached&c maximum&f heart limit."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100.0F, 1.0F);
                    return;
                }
                giveHeart(player);
                player.sendMessage(Color.colorize( PREFIX + "&fYou have gained a heart and now you have &4" + heartManager.getHearts(player) + "&f hearts."));
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 100.0F, 1.0F);
            }
        }
    }

    private void giveHeart(Player player) {
        this.heartManager.addHeart(player);
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
    }

}
