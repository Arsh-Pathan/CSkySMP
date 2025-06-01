package io.arsh.admin;

import io.arsh.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminManager implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final List<String> adminUuidStrings;

    private final Map<UUID, ArmorStand> activeAdminTags = new HashMap<>();
    private final Map<UUID, BukkitTask> activeAdminTasks = new HashMap<>();

    private static final String ADMIN_SKULL_URL = "http://textures.minecraft.net/texture/c1309f05e58a6ac605d672f99d239d63ded8b06ca443faa978d62a5008cdb512";

    public AdminManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        List<String> loadedUuids = config.getStringList("Admins");
        this.adminUuidStrings = new ArrayList<>(loadedUuids);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addAdmin(Player player) {
        String uuidString = player.getUniqueId().toString();
        if (!adminUuidStrings.contains(uuidString)) {
            adminUuidStrings.add(uuidString);
            config.set("Admins", adminUuidStrings);
            plugin.saveConfig();
            if (player.isOnline()) {
                spawnAdminTag(player);
            }
        }
    }

    public boolean isAdmin(Player player) {
        return adminUuidStrings.contains(player.getUniqueId().toString());
    }

    public void removeAdmin(Player player) {
        String uuidString = player.getUniqueId().toString();
        if (adminUuidStrings.remove(uuidString)) {
            config.set("Admins", adminUuidStrings);
            plugin.saveConfig();
            if (player.isOnline()) {
                removeAdminTag(player.getUniqueId());
            }
        }
    }

    private void spawnAdminTag(Player player) {
        if (activeAdminTags.containsKey(player.getUniqueId())) {
            removeAdminTag(player.getUniqueId());
        }
        Location location = player.getLocation();
        ArmorStand armorStand = player.getWorld().spawn(location, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setMarker(true);
        armorStand.setInvulnerable(true);
        armorStand.setSmall(true);
        armorStand.setCustomName(Color.colorize("&c&lADMIN"));
        armorStand.setCustomNameVisible(!player.isSneaking());

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setUnbreakable(true);
            skullMeta.setOwnerProfile(getProfile());
            skull.setItemMeta(skullMeta);
            armorStand.getEquipment().setHelmet(skull);
        } else {
            return;
        }
        activeAdminTags.put(player.getUniqueId(), armorStand);

        BukkitTask task = new BukkitRunnable() {
            double angle = 0.0;
            final double deltaAngle = 0.1;
            final double horizontalOffset = 0.7;
            double rotationDegrees = 0.0;
            double rotationSpeed = 15.0;
            final double maxRotationSpeed = 15.0;
            final double rotationAcceleration = 0.05;
            boolean rotateClockwise = true;

            @Override
            public void run() {
                if (!player.isOnline() || !activeAdminTags.containsKey(player.getUniqueId())) {
                    this.cancel();
                    removeAdminTag(player.getUniqueId());
                    return;
                }

                ArmorStand currentArmorStand = activeAdminTags.get(player.getUniqueId());
                if (currentArmorStand == null || currentArmorStand.isDead()) {
                    this.cancel();
                    activeAdminTasks.remove(player.getUniqueId());
                    return;
                }

                Location playerLoc = player.getLocation();
                Vector direction = playerLoc.getDirection().setY(0).normalize();

                Vector rightVector = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
                Location newLoc = playerLoc.clone().add(rightVector.multiply(horizontalOffset));

                double ySineOffset = Math.sin(angle) * 0.3;
                double playerHeightOffset = player.isSneaking() ? 1.15 : 1.45;

                newLoc.add(0, playerHeightOffset + ySineOffset, 0);
                currentArmorStand.teleport(newLoc);

                if (rotateClockwise) {
                    rotationDegrees += rotationSpeed;
                    rotationSpeed -= rotationAcceleration;
                    if (rotationSpeed <= 0.0) {
                        rotateClockwise = false;
                        rotationSpeed = 0.0;
                    }
                } else {
                    rotationDegrees -= rotationSpeed;
                    rotationSpeed += rotationAcceleration;
                    if (rotationSpeed >= maxRotationSpeed) {
                        rotationSpeed = maxRotationSpeed;
                        rotateClockwise = true;
                    }
                }

                rotationDegrees %= 360.0;
                if (rotationDegrees < 0.0) {
                    rotationDegrees += 360.0;
                }

                angle += deltaAngle;
                if (angle > Math.PI * 2) {
                    angle -= Math.PI * 2;
                }

                currentArmorStand.setHeadPose(new EulerAngle(0, Math.toRadians(rotationDegrees), 0));

                if (!player.isSneaking() && currentArmorStand.isCustomNameVisible()) {
                    currentArmorStand.getWorld().spawnParticle(Particle.ENCHANT, currentArmorStand.getLocation().add(0, 0.5, 0), // Adjusted particle pos
                            1, 0, 0, 0, 0);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        activeAdminTasks.put(player.getUniqueId(), task);
    }

    private void removeAdminTag(UUID playerUuid) {
        BukkitTask task = activeAdminTasks.remove(playerUuid);
        if (task != null) {
            task.cancel();
        }
        ArmorStand armorStand = activeAdminTags.remove(playerUuid);
        if (armorStand != null) {
            armorStand.remove();
        }
    }

    private static PlayerProfile getProfile() {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(AdminManager.ADMIN_SKULL_URL);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isAdmin(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && isAdmin(player)) {
                        spawnAdminTag(player);
                    }
                }
            }.runTaskLater(plugin, 5L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeAdminTag(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (isAdmin(player)) {
            ArmorStand armorStand = activeAdminTags.get(player.getUniqueId());
            if (armorStand != null) {
                if (event.isSneaking()) {
                    armorStand.setCustomNameVisible(false);
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (player.isOnline() && !player.isSneaking()) {
                                ArmorStand currentStand = activeAdminTags.get(player.getUniqueId());
                                if (currentStand != null) {
                                    currentStand.setCustomNameVisible(true);
                                }
                            }
                        }
                    }.runTaskLater(plugin, 1L);
                }
            }
        }
    }

    public void cleanup() {
        List<UUID> adminUuids = new ArrayList<>(activeAdminTasks.keySet());
        for (UUID uuid : adminUuids) {
            removeAdminTag(uuid);
        }
        activeAdminTasks.clear();
        activeAdminTags.clear();
    }
}