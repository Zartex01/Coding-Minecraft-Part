package com.zartex.tycoon.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Settings {

    private final String lobbyWorld;
    private final String tycoonWorld;
    private final int plotCount;
    private final int plotColumns;
    private final int autosaveSeconds;

    public Settings(JavaPlugin plugin) {
        FileConfiguration cfg = plugin.getConfig();
        lobbyWorld = cfg.getString("worlds.lobby", "world");
        tycoonWorld = cfg.getString("worlds.tycoon", "tycoon");
        plotCount = clamp(cfg.getInt("tycoon.plots", 20), 1, 100);
        plotColumns = clamp(cfg.getInt("tycoon.columns", 5), 1, 20);
        autosaveSeconds = clamp(cfg.getInt("autosave-seconds", 60), 10, 3600);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public String lobbyWorld() {
        return lobbyWorld;
    }

    public String tycoonWorld() {
        return tycoonWorld;
    }

    public int plotCount() {
        return plotCount;
    }

    public int plotColumns() {
        return plotColumns;
    }

    public int autosaveSeconds() {
        return autosaveSeconds;
    }
}
