package io.arsh.admin;

import io.arsh.utils.Color;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class AdminManager implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final List<String> adminNames;
    private final Map<String, ArmorStandData> adminArmorStands;

    public AdminManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.adminNames = new ArrayList<>(config.getStringList("Admins"));
        this.adminArmorStands = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addAdmin(Player player) {
        String playerName = player.getName();
        if (!adminNames.contains(playerName)) {
            adminNames.add(playerName);
            config.set("Admins", adminNames);
            plugin.saveConfig();
        }
    }

    public boolean isAdmin(Player player) {
        return adminNames.contains(player.getName());
    }

    public void removeAdmin(Player player) {
        String playerName = player.getName();
        if (adminNames.remove(playerName)) {
            config.set("Admins", adminNames);
            plugin.saveConfig();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isAdmin(player)) {
            removeArmorStand(player);
            ArmorStand armorStand = createArmorStand(player);
            BukkitTask task = startAnimation(player, armorStand);
            adminArmorStands.put(player.getName(), new ArmorStandData(task, armorStand));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isAdmin(player)) {
            removeArmorStand(player);
        }
    }

    private PlayerProfile getProfile() {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            URL url = new URL("http://textures.minecraft.net/texture/c1309f05e58a6ac605d672f99d239d63ded8b06ca443faa978d62a5008cdb512");
            textures.setSkin(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid skin URL", e);
        }
        profile.setTextures(textures);
        return profile;
    }

    private ArmorStand createArmorStand(Player player) {
        Location location = player.getLocation();
        ArmorStand armorStand = player.getWorld().spawn(location, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGliding(false);
        armorStand.setInvisible(true);
        armorStand.setFireTicks(-1);
        armorStand.setSmall(true);
        armorStand.setCustomName(Color.colorize("&#db0000&lA&#ed5555&lD&#fea9a9&lM&#ffd4d4&lI&#ffffff&lN"));
        armorStand.setCustomNameVisible(true);

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setUnbreakable(true);
            skullMeta.setOwnerProfile(getProfile());
            skull.setItemMeta(skullMeta);
        }
        armorStand.setHelmet(skull);
        return armorStand;
    }

    private BukkitTask startAnimation(Player player, ArmorStand armorStand) {
        return new BukkitRunnable() {
            double angle = 0.0;
            final double delta = 0.1;
            final double rightOffset = 1;
            double rotation = 0.0;
            double speed = 15.0;
            final double maxSpeed = 15.0;
            final double acceleration = 0.05;
            boolean clockwise = true;

            @Override
            public void run() {
                if (!player.isOnline() || !armorStand.isValid()) {
                    cancel();
                    return;
                }

                Location location = player.getLocation();
                Vector direction = location.getDirection();
                Location newLocation = location.clone().add(direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset));
                double yOffset = Math.sin(angle) * 0.5 + 1;
                newLocation.add(0, yOffset, 0);
                armorStand.teleport(newLocation);

                if (clockwise) {
                    rotation += speed;
                    speed -= acceleration;
                    if (speed <= 0.0) {
                        clockwise = false;
                        speed = maxSpeed;
                    }
                } else {
                    rotation -= speed;
                    speed -= acceleration;
                    if (speed <= 0.0) {
                        clockwise = true;
                        speed = maxSpeed;
                    }
                }

                if (rotation >= 360.0) rotation -= 360.0;
                else if (rotation < 0.0) rotation += 360.0;

                angle += delta;
                armorStand.setCustomNameVisible(!player.isSneaking());
                armorStand.setHeadPose(new EulerAngle(Math.toRadians(90), 0, Math.toRadians(rotation)));
                armorStand.getWorld().spawnParticle(Particle.GLOW, armorStand.getLocation().add(0, 0.8, 0.2), 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void removeArmorStand(Player player) {
        ArmorStandData data = adminArmorStands.remove(player.getName());
        if (data != null) {
            if (data.task != null) data.task.cancel();
            if (data.armorStand != null) data.armorStand.remove();
        }
    }

    private record ArmorStandData(BukkitTask task, ArmorStand armorStand) {}
}
