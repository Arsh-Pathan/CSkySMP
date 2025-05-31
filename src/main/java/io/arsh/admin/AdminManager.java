package io.arsh.admin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminManager {

    private static List<UUID> adminList;

    public AdminManager(FileConfiguration config) {
        adminList = new ArrayList<>();
        config.getStringList("Admins").forEach(uuid -> adminList.add(UUID.fromString(uuid)));
    }

    public static void addAdmin(Player player) {
        adminList.add(player.getUniqueId());
    }

    public static boolean isAdmin(Player player) {
        return adminList.contains(player.getUniqueId());
    }

    public static void removeAdmin(Player player) {
        adminList.remove(player.getUniqueId());
    }

}
