package io.arsh.combatlog;

import io.arsh.lifesteal.HeartManager;
import io.arsh.utils.Color;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLog implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final HeartManager heartManager;
    private final Map<UUID, BukkitTask> combatLogs;
    private final String PREFIX = "&5&lS&d&lM&f&lP ";

    public CombatLog(JavaPlugin plugin, HeartManager heartManager) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.heartManager = heartManager;
        this.combatLogs = new HashMap<>();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getDamager() instanceof Player damager)) return;
        combatLog(player);
        combatLog(damager);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (combatLogs.containsKey(player.getUniqueId())) {
            if (heartManager.getHearts(player) > 1) {
                heartManager.dropHeart(player);
            }
            dropItem(player);
            BukkitTask task = combatLogs.remove(player.getUniqueId());
            if (task != null) {
                task.cancel();
            }
        }
    }

    private void combatLog(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = combatLogs.get(uuid);
        if (task != null) {
            task.cancel();
        } else {
            String message = PREFIX + "&fYou are now in combat log. &5[&fDO NOT LEAVE OR YOU WILL BE KICKED!&5]&f";
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_WOLF, 100.0F, 0.0F);
            player.sendMessage(Color.colorize(message));
        }
        task = (new BukkitRunnable() {
            public void run() {
                combatLogs.remove(uuid);
                String message = PREFIX + "&fYou are now out of combat log.";
                player.sendMessage(Color.colorize(message));
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_UNEQUIP_WOLF, 100.0F, 0.0F);
            }
        }).runTaskLater(plugin, config.getInt("Combat-Log.Duration") * 20L);
        combatLogs.put(uuid, task);
    }


    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (combatLogs.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String message = PREFIX + "&fYou can't use commands while in combat log.";
            player.sendMessage(Color.colorize(message));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 100.0F, 0.0F);
        }
    }

    private void dropItem(Player player) {
        for(ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
        player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getTotalExperience());
        player.setExp(0.0F);
        player.getInventory().clear();
    }

}
