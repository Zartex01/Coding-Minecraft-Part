package com.zartex.tycoon.listener;

import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PlotManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class ProtectionListener implements Listener {

    private final PlotManager plots;
    private final DataManager data;

    public ProtectionListener(PlotManager plots, DataManager data) {
        this.plots = plots;
        this.data = data;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (plots.isTycoonWorld(e.getBlock().getWorld()) && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (plots.isTycoonWorld(e.getBlock().getWorld()) && !e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p) || !plots.isTycoonWorld(p.getWorld())) return;
        e.setCancelled(true);
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            PlayerData d = data.get(p.getUniqueId());
            if (d != null) p.teleport(plots.home(d.getPlot()));
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (plots.isTycoonWorld(e.getEntity().getWorld())) e.setCancelled(true);
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (plots.isTycoonWorld(e.getLocation().getWorld())
                && e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            e.setCancelled(true);
        }
    }
}
