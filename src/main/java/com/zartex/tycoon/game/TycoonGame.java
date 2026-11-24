package com.zartex.tycoon.game;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.item.MenuItems;
import com.zartex.tycoon.model.MachineType;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PlotManager;
import com.zartex.tycoon.plot.PlotRegistry;
import com.zartex.tycoon.util.PlayerPrep;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TycoonGame {

    private final WorldService worlds;
    private final DataManager data;
    private final PlotManager plots;
    private final PlotRegistry registry;
    private final BoardManager boards;
    private final MenuItems items;

    public TycoonGame(WorldService worlds, DataManager data, PlotManager plots,
                      PlotRegistry registry, BoardManager boards, MenuItems items) {
        this.worlds = worlds;
        this.data = data;
        this.plots = plots;
        this.registry = registry;
        this.boards = boards;
        this.items = items;
    }

    public CreateResult create(Player p) {
        if (!worlds.isTycoon(p.getWorld())) return CreateResult.WRONG_WORLD;
        UUID id = p.getUniqueId();

        if (registry.plotOf(id) >= 0) {
            teleportHome(p);
            return CreateResult.ALREADY_HAS_PLOT;
        }
        int plot = registry.assign(id);
        if (plot < 0) return CreateResult.FULL;

        PlayerData d = data.getOrCreate(id);
        plots.claim(plot, p.getName(), d);
        p.teleport(plots.home(plot));
        boards.setup(p);
        return CreateResult.CREATED;
    }

    /** Saves progress and frees the plot. Safe to call when the player has no plot. */
    public void leave(Player p) {
        UUID id = p.getUniqueId();
        int plot = registry.plotOf(id);
        if (plot < 0) return;
        registry.release(id);
        plots.release(plot);
        boards.remove(p);
        data.saveAsync();
    }

    public void sendToSpawn(Player p) {
        p.teleport(plots.spawn());
        prepareSpawn(p);
    }

    public void prepareSpawn(Player p) {
        PlayerPrep.reset(p);
        items.give(p, 8, items.backToLobby(), MenuItems.BACK);
    }

    public void teleportHome(Player p) {
        int plot = registry.plotOf(p.getUniqueId());
        p.teleport(plot >= 0 ? plots.home(plot) : plots.spawn());
    }

    public boolean reset(Player p) {
        int plot = registry.plotOf(p.getUniqueId());
        if (plot < 0) return false;
        PlayerData d = data.getOrCreate(p.getUniqueId());
        d.reset();
        for (MachineType type : MachineType.values()) {
            plots.renderMachine(plot, type, d.owns(type));
        }
        boards.update(p);
        return true;
    }

    public boolean giveMoney(Player p, long amount) {
        if (registry.plotOf(p.getUniqueId()) < 0) return false;
        data.getOrCreate(p.getUniqueId()).addMoney(amount);
        boards.update(p);
        return true;
    }

    public int plotNumber(Player p) {
        return registry.plotOf(p.getUniqueId()) + 1;
    }

    public int freePlots() {
        return registry.freeCount();
    }

    public int totalPlots() {
        return registry.capacity();
    }
}
