package com.zartex.tycoon.lobby;

import com.zartex.tycoon.game.TycoonGame;
import com.zartex.tycoon.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MinigameMenu {

    public static final int TYCOON_SLOT = 13;

    public static class MenuHolder implements InventoryHolder {
        private Inventory inventory;

        void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }

    private final TycoonGame game;

    public MinigameMenu(TycoonGame game) {
        this.game = game;
    }

    public void open(Player p) {
        MenuHolder holder = new MenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, Text.component("&6&lMinigames"));
        holder.setInventory(inv);

        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.displayName(Text.item(" "));
        pane.setItemMeta(paneMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, pane);

        ItemStack tycoon = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = tycoon.getItemMeta();
        meta.displayName(Text.item("&6&lTycoon"));
        meta.lore(List.of(
                Text.item("&7Build your own factory"),
                Text.item("&7and earn money!"),
                Text.item(""),
                Text.item("&eFree plots: &f" + game.freePlots() + "/" + game.totalPlots()),
                Text.item(""),
                Text.item("&aClick to play!")));
        tycoon.setItemMeta(meta);
        inv.setItem(TYCOON_SLOT, tycoon);

        p.openInventory(inv);
    }
}
