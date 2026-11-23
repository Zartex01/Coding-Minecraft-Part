package com.zartex.tycoon.data;

import com.zartex.tycoon.model.MachineType;
import com.zartex.tycoon.model.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private int nextPlot;
    private int groundY;
    private boolean hasGroundY;

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
    }

    public void load() {
        plugin.getDataFolder().mkdirs();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        nextPlot = yaml.getInt("nextPlot", 0);
        if (yaml.contains("groundY")) {
            groundY = yaml.getInt("groundY");
            hasGroundY = true;
        }
        ConfigurationSection section = yaml.getConfigurationSection("players");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                PlayerData d = new PlayerData(section.getInt(key + ".plot"));
                d.setMoney(section.getLong(key + ".money"));
                for (int i : section.getIntegerList(key + ".owned")) {
                    if (i >= 0 && i < MachineType.count()) {
                        d.setOwned(MachineType.fromIndex(i), true);
                    }
                }
                players.put(UUID.fromString(key), d);
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Skipped invalid player entry: " + key);
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("nextPlot", nextPlot);
        if (hasGroundY) yaml.set("groundY", groundY);
        for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
            String key = "players." + entry.getKey();
            PlayerData d = entry.getValue();
            yaml.set(key + ".money", d.getMoney());
            yaml.set(key + ".plot", d.getPlot());
            java.util.List<Integer> owned = new java.util.ArrayList<>();
            for (MachineType type : MachineType.values()) {
                if (d.owns(type)) owned.add(type.index());
            }
            yaml.set(key + ".owned", owned);
        }
        try {
            yaml.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not save data.yml: " + ex.getMessage());
        }
    }

    public PlayerData get(UUID id) {
        return players.get(id);
    }

    public boolean has(UUID id) {
        return players.containsKey(id);
    }

    public PlayerData create(UUID id) {
        PlayerData d = new PlayerData(nextPlot++);
        d.setOwned(MachineType.COPPER, true);
        players.put(id, d);
        return d;
    }

    public boolean hasGroundY() {
        return hasGroundY;
    }

    public int getGroundY() {
        return groundY;
    }

    public void setGroundY(int groundY) {
        this.groundY = groundY;
        this.hasGroundY = true;
    }
}
