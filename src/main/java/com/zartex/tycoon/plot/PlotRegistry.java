package com.zartex.tycoon.plot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Tracks which player currently occupies which plot. Main thread only. */
public class PlotRegistry {

    private final UUID[] byPlot;
    private final Map<UUID, Integer> byPlayer = new HashMap<>();

    public PlotRegistry(int capacity) {
        this.byPlot = new UUID[capacity];
    }

    /** Gives the first free plot, or -1 if all are taken. */
    public int assign(UUID id) {
        Integer existing = byPlayer.get(id);
        if (existing != null) return existing;
        for (int i = 0; i < byPlot.length; i++) {
            if (byPlot[i] == null) {
                byPlot[i] = id;
                byPlayer.put(id, i);
                return i;
            }
        }
        return -1;
    }

    public int plotOf(UUID id) {
        return byPlayer.getOrDefault(id, -1);
    }

    public void release(UUID id) {
        Integer plot = byPlayer.remove(id);
        if (plot != null) byPlot[plot] = null;
    }

    public int capacity() {
        return byPlot.length;
    }

    public int freeCount() {
        return byPlot.length - byPlayer.size();
    }

    public Map<UUID, Integer> snapshot() {
        return new HashMap<>(byPlayer);
    }
}
