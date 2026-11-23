package com.zartex.tycoon;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.command.TycoonCommand;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.listener.JoinQuitListener;
import com.zartex.tycoon.listener.PadListener;
import com.zartex.tycoon.listener.ProtectionListener;
import com.zartex.tycoon.plot.PlotManager;
import com.zartex.tycoon.shop.MachineShop;
import com.zartex.tycoon.task.IncomeTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TycoonPlugin extends JavaPlugin {

    private DataManager data;

    @Override
    public void onEnable() {
        data = new DataManager(this);
        data.load();

        PlotManager plots = new PlotManager(data);
        if (!plots.init()) {
            getLogger().severe("Could not create the tycoon world!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        BoardManager boards = new BoardManager(data);
        MachineShop shop = new MachineShop(data, plots, boards);

        getServer().getPluginManager().registerEvents(new JoinQuitListener(this, data, plots, boards), this);
        getServer().getPluginManager().registerEvents(new PadListener(plots, shop), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(plots, data), this);

        if (getCommand("tycoon") != null) {
            getCommand("tycoon").setExecutor(new TycoonCommand(data, plots, boards));
        }

        Bukkit.getScheduler().runTaskTimer(this, new IncomeTask(data, plots, boards), 20L, 20L);
        Bukkit.getScheduler().runTaskTimer(this, data::save, 6000L, 6000L);
    }

    @Override
    public void onDisable() {
        if (data != null) data.save();
    }
}
