package com.zartex.tycoon.world;

import com.zartex.tycoon.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class WorldService {

    private final Settings settings;
    private World lobby;
    private World tycoon;

    public WorldService(Settings settings) {
        this.settings = settings;
    }

    /** Returns false if a world could not be loaded. */
    public boolean init() {
        lobby = Bukkit.getWorld(settings.lobbyWorld());
        if (lobby == null && !Bukkit.getWorlds().isEmpty()) {
            lobby = Bukkit.getWorlds().get(0);
        }
        tycoon = Bukkit.getWorld(settings.tycoonWorld());
        if (tycoon == null) {
            tycoon = new WorldCreator(settings.tycoonWorld())
                    .type(WorldType.FLAT)
                    .generateStructures(false)
                    .createWorld();
        }
        return lobby != null && tycoon != null && !lobby.equals(tycoon);
    }

    public World lobby() {
        return lobby;
    }

    public World tycoon() {
        return tycoon;
    }

    public boolean isLobby(World w) {
        return w != null && w.equals(lobby);
    }

    public boolean isTycoon(World w) {
        return w != null && w.equals(tycoon);
    }

    public boolean isManaged(World w) {
        return isLobby(w) || isTycoon(w);
    }

    public void keepDaytime() {
        for (World w : new World[]{lobby, tycoon}) {
            w.setTime(6000);
            w.setStorm(false);
        }
    }
}
