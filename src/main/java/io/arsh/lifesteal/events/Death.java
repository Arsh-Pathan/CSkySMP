package io.arsh.lifesteal.events;

import io.arsh.lifesteal.HeartManager;
import io.arsh.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Death implements Listener {

    private final HeartManager heartManager;
    private final String PREFIX = "&4&lS&c&lM&f&lP ";

    public Death(HeartManager heartManager) {
        this.heartManager = heartManager;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer != null) {
            if (heartManager.hasHaxHearts(killer)) {
                killer.sendMessage(Color.colorize( PREFIX + "&fYou have reached&4 maximum&f heart limit. So you can't obtain heart of this player."));
                killer.playSound(killer.getLocation(), Sound.ENTITY_VILLAGER_NO, 100.0F, 1.0F);
            } else
            if (heartManager.getHearts(victim) == 1) {
                killer.sendMessage(Color.colorize( PREFIX + "&fThe player you just killed only has &4one &fheart left. So you can't obtain heart of this player."));
                killer.playSound(killer.getLocation(), Sound.ENTITY_VILLAGER_NO, 100.0F, 1.0F);
            }
            heartManager.dropHeart(victim, 1);
            event.setDeathMessage(Color.colorize("&4" + victim.getName() + "&f has been killed by &4" + killer.getName() + "&f."));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player == killer) continue;
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 100.0F, 1.0F);

            }
        }
    }

}
