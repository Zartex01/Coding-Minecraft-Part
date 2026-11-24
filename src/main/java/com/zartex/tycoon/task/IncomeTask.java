package com.zartex.tycoon.task;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PlotRegistry;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IncomeTask implements Runnable {

    private final WorldService worlds;
    private final DataManager data;
    private final PlotRegistry registry;
    private final BoardManager boards;

    public IncomeTask(WorldService worlds, DataManager data, PlotRegistry registry, BoardManager boards) {
        this.worlds = worlds;
        this.data = data;
        this.registry = registry;
        this.boards = boards;
    }

    @Override
    public void run() {
        worlds.keepDaytime();
        // Only players who currently occupy a plot earn money.
        for (UUID id : registry.snapshot().keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p == null) continue;
            PlayerData d = data.get(id);
            if (d == null) continue;
            d.addMoney(d.income());
            boards.update(p);
        }
    }
}
