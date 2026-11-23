package com.zartex.tycoon.listener;

import com.zartex.tycoon.plot.PadInfo;
import com.zartex.tycoon.plot.PlotManager;
import com.zartex.tycoon.shop.MachineShop;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PadListener implements Listener {

    private static final long COOLDOWN_MS = 1000;

    private final PlotManager plots;
    private final MachineShop shop;
    private final Map<UUID, Long> lastUse = new HashMap<>();

    public PadListener(PlotManager plots, MachineShop shop) {
        this.plots = plots;
        this.shop = shop;
    }

    @EventHandler
    public void onPlate(PlayerInteractEvent e) {
        if (e.getAction() != Action.PHYSICAL) return;
        Block block = e.getClickedBlock();
        if (block == null) return;

        PadInfo pad = plots.findPad(block);
        if (pad == null) return;

        Player p = e.getPlayer();
        long now = System.currentTimeMillis();
        Long last = lastUse.get(p.getUniqueId());
        if (last != null && now - last < COOLDOWN_MS) return;
        lastUse.put(p.getUniqueId(), now);

        shop.tryBuy(p, pad);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        lastUse.remove(e.getPlayer().getUniqueId());
    }
}
