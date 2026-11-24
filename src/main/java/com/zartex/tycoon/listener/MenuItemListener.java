package com.zartex.tycoon.listener;

import com.zartex.tycoon.item.MenuItems;
import com.zartex.tycoon.lobby.LobbyManager;
import com.zartex.tycoon.lobby.MinigameMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MenuItemListener implements Listener {

    private final MenuItems items;
    private final MinigameMenu menu;
    private final LobbyManager lobby;

    public MenuItemListener(MenuItems items, MinigameMenu menu, LobbyManager lobby) {
        this.items = items;
        this.menu = menu;
        this.lobby = lobby;
    }

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = e.getItem();
        if (item == null) return;

        if (items.is(item, MenuItems.COMPASS)) {
            e.setCancelled(true);
            menu.open(e.getPlayer());
        } else if (items.is(item, MenuItems.BACK)) {
            e.setCancelled(true);
            lobby.send(e.getPlayer());
        }
    }
}
