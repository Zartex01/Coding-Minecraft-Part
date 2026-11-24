package com.zartex.tycoon.item;

import com.zartex.tycoon.util.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MenuItems {

    public static final String COMPASS = "minigame_compass";
    public static final String BACK = "back_to_lobby";

    private final NamespacedKey key;

    public MenuItems(JavaPlugin plugin) {
        this.key = new NamespacedKey(plugin, "menu_item");
    }

    public ItemStack compass() {
        return build(Material.COMPASS, "&6&lMinigames", COMPASS, "&7Right-click to choose a minigame");
    }

    public ItemStack backToLobby() {
        return build(Material.RED_BED, "&c&lBack to lobby", BACK, "&7Right-click to return to the lobby");
    }

    private ItemStack build(Material material, String name, String id, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Text.item(name));
        meta.lore(List.of(Text.item(lore)));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    public boolean is(ItemStack item, String id) {
        if (item == null || !item.hasItemMeta()) return false;
        return id.equals(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
    }

    public boolean has(Player p, String id) {
        for (ItemStack stack : p.getInventory().getContents()) {
            if (is(stack, id)) return true;
        }
        return false;
    }

    public void give(Player p, int slot, ItemStack item, String id) {
        if (!has(p, id)) p.getInventory().setItem(slot, item);
    }
}
