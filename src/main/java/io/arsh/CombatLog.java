package io.arsh;

import io.arsh.utils.ActionBar;
import io.arsh.utils.Color;
import io.arsh.utils.SmallText;
import io.arsh.utils.Title;
import org.bukkit.Sound;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CombatLog implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, BukkitTask> combatLogs;
    private final Map<UUID, String> lastDamager;
    private final Set<UUID> combatQuitters;

    public CombatLog(JavaPlugin plugin) {
        this.plugin = plugin;
        this.combatLogs = new HashMap<>();
        this.lastDamager = new HashMap<>();
        this.combatQuitters = new HashSet<>();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player) || !(event.getDamager() instanceof Player damager)) return;

        lastDamager.put(player.getUniqueId(), damager.getName());

        combatLog(player);
        combatLog(damager);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (combatQuitters.contains(player.getUniqueId())) {
            String killerName = lastDamager.getOrDefault(player.getUniqueId(), "Unknown");
            Title.send(player, "&f☠ &7&lʏᴏᴜ ᴅɪᴇᴅ ᴛᴏ ᴄᴏᴍʙᴀᴛ ʟᴏɢ! &f☠", "&8ᴡʜɪʟᴇ ꜰɪɢʜᴛɪɴɢ ᴡɪᴛʜ &7" + SmallText.smallizier(killerName) + "&8... ɴᴏᴏʙ", 10, 60, 10);
            player.sendMessage(Color.colorize("&c[ᴄᴏᴍʙᴀᴛ ʟᴏɢ] &fʏᴏᴜ ᴅɪᴇᴅ ᴛᴏ ᴄᴏᴍʙᴀᴛ ʟᴏɢ ᴡʜɪʟᴇ ꜰɪɢʜᴛɪɴɢ ᴡɪᴛʜ &c" + SmallText.smallizier(killerName) + "&f. &oᴡʜᴀᴛ ᴀ ɴᴏᴏʙ."));
            combatQuitters.remove(player.getUniqueId());
            lastDamager.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (combatLogs.containsKey(player.getUniqueId())) {
            dropItem(player);

            combatQuitters.add(player.getUniqueId());

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
            ActionBar.send(player, Color.colorize("&9ɴᴏᴡ ɪɴ ᴄᴏᴍʙᴀᴛ ʟᴏɢ!"));
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_WOLF, 100.0F, 1.0F);
        }

        final int combatTime = 60;

        task = new BukkitRunnable() {
            int timeLeft = combatTime;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    combatLogs.remove(uuid);
                    ActionBar.send(player, Color.colorize("&9ᴏᴜᴛ ᴏꜰ ᴄᴏᴍʙᴀᴛ ʟᴏɢ!"));
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_UNEQUIP_WOLF, 100.0F, 1.0F);
                    cancel();
                    return;
                }

                ActionBar.send(player, Color.colorize("&cᴄᴏᴍʙᴀᴛ ʟᴏɢ&f: " + SmallText.smallizier(String.valueOf(timeLeft)) + "&cѕ"));
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        combatLogs.put(uuid, task);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (combatLogs.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String message = "&c[ᴄᴏᴍʙᴀᴛ ʟᴏɢ] &fʏᴏᴜ ᴄᴀɴ'ᴛ ᴜѕᴇ ᴄᴏᴍᴍᴀɴᴅѕ ᴡʜɪʟᴇ ɪɴ ᴄᴏᴍʙᴀᴛ ʟᴏɢ.";
            player.sendMessage(Color.colorize(message));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 100.0F, 0.0F);
        }
    }

    private void dropItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
        player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getTotalExperience());
        player.setExp(0.0F);
        player.getInventory().clear();
    }
}
