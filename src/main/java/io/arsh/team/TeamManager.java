package io.arsh.team;

import io.arsh.team.commands.TeamCommand;
import io.arsh.utils.Color;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeamManager {
    private final JavaPlugin plugin;
    private final Map<UUID, TeamData> teamData;
    private FileConfiguration data;
    private InviteManager inviteManager;

    public TeamManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.teamData = new HashMap<>();
        this.inviteManager = new InviteManager(plugin, this);
    }

    public void initialize() {
        loadTeamData();
        plugin.getCommand("team").setExecutor(new TeamCommand(plugin, this, inviteManager));
    }
    
    public void loadTeamData() {
        File file = new File(plugin.getDataFolder(), "team.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("team.yml", false);
        }
        this.data = YamlConfiguration.loadConfiguration(file);
        for (String key : data.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String name = data.getString(key + ".Name");
            Color color = Color.valueOf(data.getString(key + ".Color"));
            String symbol = data.getString(key + ".Symbol");
            OfflinePlayer leader = plugin.getServer().getOfflinePlayer(UUID.fromString(data.getString(key + ".Leader")));
            List<OfflinePlayer> members = data.getStringList(key + ".Members").stream().map(UUID::fromString).map(plugin.getServer()::getOfflinePlayer).toList();
            Location base = data.getLocation(key + ".Base");
            teamData.put(uuid, new TeamData(uuid, name, color, symbol, leader, members, base));
        }
    }
    
    public TeamData getTeamData(OfflinePlayer player) {
        for (TeamData data : teamData.values()) {
            if (data.getMembers().contains(player)) {
                return data;
            }
        }
        return null;
    }

    public void createTeam(String name, Color color, String symbol, OfflinePlayer leader) {
        UUID uuid = UUID.randomUUID();
        teamData.put(uuid, new TeamData(uuid, name, color, symbol, leader, List.of(leader), null));
        data.set(uuid + ".Name", name);
        data.set(uuid + ".Color", color.toString());
        data.set(uuid + ".Symbol", symbol);
        data.set(uuid + ".Leader", leader.getUniqueId().toString());
        data.set(uuid + ".Members", List.of(leader.getUniqueId().toString()));
        data.set(uuid + ".Base", null);
        saveData();
    }

    public boolean teamExist(String name) {
        for (TeamData data : teamData.values()) {
            if (data.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void disbandTeam(UUID uuid) {
        teamData.remove(uuid);
        data.set(uuid + ".Name", null);
        data.set(uuid + ".Color", null);
        data.set(uuid + ".Symbol", null);
        data.set(uuid + ".Leader", null);
        data.set(uuid + ".Members", null);
        data.set(uuid + ".Base", null);
        saveData();
    }

    public void setTeamName(UUID uuid, String name) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.setName(name);
            this.data.set(uuid + ".Name", name);
        }
    }

    public void setTeamColor(UUID uuid, Color color) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.setColor(color);
            this.data.set(uuid + ".Color", color.toString());
        }
    }

    public void setTeamSymbol(UUID uuid, String symbol) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.setSymbol(symbol);
            this.data.set(uuid + ".Symbol", symbol);
        }
    }

    public void setTeamLeader(UUID uuid, OfflinePlayer player) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.setLeader(player);
            this.data.set(uuid + ".Leader", player.getUniqueId().toString());
        }
    }

    public void addMember(UUID uuid, OfflinePlayer player) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.addMember(player);
            this.data.set(uuid + ".Members", data.getMembers().stream().map(OfflinePlayer::getUniqueId).map(UUID::toString).toList());
        }
    }

    public void removeMember(UUID uuid, OfflinePlayer player) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.removeMember(player);
        }
    }

    public void setTeamBase(UUID uuid, Location base) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.setBase(base);
            this.data.set(uuid + ".Base", base);
        }
    }

    public void removeTeamBase(UUID uuid) {
        TeamData data = teamData.get(uuid);
        if (data != null) {
            data.setBase(null);
            this.data.set(uuid + ".Base", null);
        }
    }

    private void saveData() {
        try {
            data.save(new File(plugin.getDataFolder(), "team.yml"));
        } catch (Exception ex) {
            plugin.getLogger().severe(ex.getMessage());
        }
    }
    
    public boolean hasTeam(OfflinePlayer player) {
        return getTeamData(player) != null;
    }
    
    public static class TeamData {
        private final UUID uuid;
        private String name;
        private Color color;
        private String symbol;
        private OfflinePlayer leader;
        private final List<OfflinePlayer> members;
        private Location base;

        public TeamData(UUID uuid, String name, Color color, String symbol, OfflinePlayer leader, List<OfflinePlayer> members, Location base) {
            this.uuid = uuid;
            this.name = name;
            this.color = color;
            this.symbol = symbol;
            this.leader = leader;
            this.members = members;
            this.base = base;
        }

        public UUID getUUID() {
            return uuid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public ChatColor getColor() {
            return Color.getChatColor(color);
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setLeader(OfflinePlayer player) {
            this.leader = player;
        }

        public OfflinePlayer getLeader() {
            return leader;
        }

        public void addMember(OfflinePlayer player) {
            members.add(player);
        }

        public List<OfflinePlayer> getMembers() {
            return members;
        }

        public void removeMember(OfflinePlayer player) {
            members.remove(player);
        }

        public void setBase(Location base) {
            this.base = base;
        }

        public boolean hasBase() {
            return base != null;
        }

        public Location getBase() {
            return base;
        }
    }

}
