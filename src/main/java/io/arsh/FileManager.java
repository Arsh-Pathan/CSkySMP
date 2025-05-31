package io.arsh;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    public JavaPlugin plugin;
    public Map<String, FileConfiguration> files;

    public FileManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.files = new HashMap<>();
    }

    public void initialize() {
        loadFile("teams");
    }

    private void loadFile(String name) {
        try {
            File file = new File(plugin.getDataFolder(), name + ".yml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                plugin.saveResource(name + ".yml", false);
            }
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            files.put(name, fileConfiguration);
            
        } catch (Exception ex) {
            plugin.getLogger().severe("An error occurred while loading file " + plugin.getDataFolder() + File.separator + name + ".yml.");
        }
        
    }

    public FileConfiguration getFile(String name) {
        return files.get(name);
    }

    public void saveFile(String name) {
        if (files.containsKey(name)) {
            try {
                files.get(name).save(new File(plugin.getDataFolder(), name + ".yml"));
            } catch (IOException ex) {
                plugin.getLogger().severe("An error occurred while saving file " + plugin.getDataFolder() + File.separator + name + ".yml.");
            }
        }
    }
    
    public void reload(String name) {
        saveFile(name);
        loadFile(name);
    }

}
