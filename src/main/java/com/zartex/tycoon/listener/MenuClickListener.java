package com.zartex.tycoon.listener;

import com.zartex.tycoon.game.TycoonGame;
import com.zartex.tycoon.lobby.MinigameMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class MenuClickListener implements Listener {

    private final TycoonGame game;

    public MenuClickListener(TycoonGame game) {
        this.game = game;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof MinigameMenu.MenuHolder)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (e.getRawSlot() == MinigameMenu.TYCOON_SLOT) {
            p.closeInventory();
            game.sendToSpawn(p);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof MinigameMenu.MenuHolder) {
            e.setCancelled(true);
        }
    }
}
