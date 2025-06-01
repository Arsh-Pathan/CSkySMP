package io.arsh.admin;

import io.arsh.utils.Color;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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
import java.util.UUID;

public class Arsh implements Listener {

    private ArmorStand armorStand;
    private BukkitTask runnable;
    private final JavaPlugin plugin;

    public Arsh(JavaPlugin plugin) {
        this.plugin = plugin;
        cleanupStrayArmorStands();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getDisplayName().equals("ArshGamer")) {
            cleanupStrayArmorStands();
            armorStand = summonCSky(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (event.getPlayer().getDisplayName().equals("ArshGamer")) {
            cleanupStrayArmorStands();
        }
    }

    private static PlayerProfile getProfile() {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL("http://textures.minecraft.net/texture/c1309f05e58a6ac605d672f99d239d63ded8b06ca443faa978d62a5008cdb512");
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

    private ArmorStand summonCSky(Player player) {
        Location playerLoc = player.getLocation();
        ArmorStand csky = player.getWorld().spawn(playerLoc, ArmorStand.class);
        csky.setVisible(false);
        csky.setGravity(false);
        csky.setFireTicks(-1);
        csky.setInvulnerable(true);
        csky.setSmall(true);
        csky.setCustomName(Color.colorize("&#db0000&lA&#ed5555&lD&#fea9a9&lM&#ffd4d4&lI&#ffffff&lN"));
        csky.setCustomNameVisible(true);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setUnbreakable(true);
        skullMeta.setOwnerProfile(getProfile());
        skull.setItemMeta(skullMeta);
        csky.setSmall(true);
        csky.setHelmet(skull);

        runnable = new BukkitRunnable() {
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
                Location playerLoc = player.getLocation();
                Vector direction = playerLoc.getDirection();
                Location newLoc = playerLoc.clone().add(direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(rightOffset));
                double yOffset = Math.sin(angle) * 0.5 + 1;
                newLoc.add(0, yOffset, 0);
                csky.teleport(newLoc);
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
                if (rotation >= 360.0) {
                    rotation -= 360.0;
                } else if (rotation < 0.0) {
                    rotation += 360.0;
                }
                angle += delta;
                armorStand.setCustomNameVisible(!player.isSneaking());
                csky.setHeadPose(new EulerAngle(Math.toRadians(90), 0, Math.toRadians(rotation)));
                csky.getWorld().spawnParticle(Particle.GLOW, csky.getLocation().add(0, 0.8, 0.2), 0);
            }
        }.runTaskTimer(plugin, 0L, 1L);
        return csky;
    }

    private void cleanupStrayArmorStands() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand as) {
                    if (as.getName().equals(armorStand.getName())) {
                        as.remove();
                        if (runnable != null) {
                            runnable.cancel();
                        }
                    }
                }
            }
        }
    }

}