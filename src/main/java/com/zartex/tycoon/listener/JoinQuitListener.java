package com.zartex.tycoon.listener;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PlotManager;
import com.zartex.tycoon.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinQuitListener implements Listener {

    private final JavaPlugin plugin;
    private final DataManager data;
    private final PlotManager plots;
    private final BoardManager boards;

    public JoinQuitListener(JavaPlugin plugin, DataManager data, PlotManager plots, BoardManager boards) {
        this.plugin = plugin;
        this.data = data;
        this.plots = plots;
        this.boards = boards;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerData d = data.get(p.getUniqueId());
        if (d == null) {
            d = data.create(p.getUniqueId());
            plots.buildPlot(d);
            data.save();
        }
        final int plot = d.getPlot();
        boards.setup(p);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            p.teleport(plots.home(plot));
            if (!p.isOp()) p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage(Text.color("&6Welcome to Tycoon! &7Step on the red pads to buy machines."));
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        boards.remove(e.getPlayer().getUniqueId());
        data.save();
    }
}
