package com.zartex.tycoon.data;

import com.zartex.tycoon.model.MachineType;
import com.zartex.tycoon.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, PlayerData> players = new HashMap<>();

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
    }

    public void load() {
        plugin.getDataFolder().mkdirs();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = yaml.getConfigurationSection("players");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                PlayerData d = new PlayerData();
                d.setMoney(section.getLong(key + ".money"));
                for (int i : section.getIntegerList(key + ".owned")) {
                    if (i >= 0 && i < MachineType.count()) {
                        d.setOwned(MachineType.fromIndex(i), true);
                    }
                }
                players.put(id, d);
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Skipped invalid player entry: " + key);
            }
        }
    }

    public PlayerData get(UUID id) {
        return players.get(id);
    }

    public PlayerData getOrCreate(UUID id) {
        return players.computeIfAbsent(id, k -> new PlayerData());
    }

    private String snapshot() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
            String key = "players." + entry.getKey();
            PlayerData d = entry.getValue();
            yaml.set(key + ".money", d.getMoney());
            List<Integer> owned = new ArrayList<>();
            for (MachineType type : MachineType.values()) {
                if (d.owns(type)) owned.add(type.index());
            }
            yaml.set(key + ".owned", owned);
        }
        return yaml.saveToString();
    }

    /** Builds the data on the main thread, writes the file in the background. */
    public void saveAsync() {
        String content = snapshot();
        if (!plugin.isEnabled()) {
            write(content);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> write(content));
    }

    public void saveNow() {
        write(snapshot());
    }

    private synchronized void write(String content) {
        try {
            Path target = file.toPath();
            Path tmp = target.resolveSibling("data.yml.tmp");
            Files.writeString(tmp, content);
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not save data.yml: " + ex.getMessage());
        }
    }
}
