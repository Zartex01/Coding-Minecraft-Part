package com.zartex.tycoon.shop;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.MachineType;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PadInfo;
import com.zartex.tycoon.plot.PlotManager;
import com.zartex.tycoon.plot.PlotRegistry;
import com.zartex.tycoon.util.Text;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MachineShop {

    private final DataManager data;
    private final PlotRegistry registry;
    private final PlotManager plots;
    private final BoardManager boards;

    public MachineShop(DataManager data, PlotRegistry registry, PlotManager plots, BoardManager boards) {
        this.data = data;
        this.registry = registry;
        this.plots = plots;
        this.boards = boards;
    }

    public void tryBuy(Player p, PadInfo pad) {
        int myPlot = registry.plotOf(p.getUniqueId());
        if (myPlot != pad.plot()) {
            p.sendMessage(Text.color("&cThis is not your plot!"));
            return;
        }
        PlayerData d = data.get(p.getUniqueId());
        if (d == null) return;

        MachineType type = pad.type();
        if (d.owns(type)) return;

        MachineType previous = type.previous();
        if (previous != null && !d.owns(previous)) {
            p.sendMessage(Text.color("&cBuy the previous machine first!"));
            return;
        }
        if (d.getMoney() < type.getPrice()) {
            p.sendMessage(Text.color("&cNot enough money! You need &e$" + Text.money(type.getPrice())));
            return;
        }

        d.addMoney(-type.getPrice());
        d.setOwned(type, true);
        plots.renderMachine(myPlot, type, true);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        p.sendMessage(Text.color("&aYou bought the " + type.getDisplayName()
                + "! &7(+$" + type.getIncome() + "/s)"));
        boards.update(p);
    }
}
