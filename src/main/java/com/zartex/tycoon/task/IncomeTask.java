package com.zartex.tycoon.task;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PlotManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IncomeTask implements Runnable {

    private final DataManager data;
    private final PlotManager plots;
    private final BoardManager boards;

    public IncomeTask(DataManager data, PlotManager plots, BoardManager boards) {
        this.data = data;
        this.plots = plots;
        this.boards = boards;
    }

    @Override
    public void run() {
        plots.keepDaytime();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData d = data.get(p.getUniqueId());
            if (d == null) continue;
            d.addMoney(d.income());
            boards.update(p);
        }
    }
}
