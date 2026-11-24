package com.zartex.tycoon.lobby;

import com.zartex.tycoon.item.MenuItems;
import com.zartex.tycoon.util.PlayerPrep;
import com.zartex.tycoon.util.WorldUtil;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LobbyManager {

    private static final int HALF = 12;

    private final WorldService worlds;
    private final MenuItems items;
    private Location spawn;

    public LobbyManager(WorldService worlds, MenuItems items) {
        this.worlds = worlds;
        this.items = items;
    }

    /** Builds a sky platform with invisible walls and sets the world spawn on it. */
    public void init() {
        World world = worlds.lobby();
        int y = Math.min(250, world.getMaxHeight() - 10);

        WorldUtil.forceLoad(world, -HALF - 1, -HALF - 1, HALF + 1, HALF + 1);
        for (int dx = -HALF; dx <= HALF; dx++) {
            for (int dz = -HALF; dz <= HALF; dz++) {
                boolean border = Math.abs(dx) == HALF || Math.abs(dz) == HALF;
                WorldUtil.setBlock(world, dx, y, dz,
                        border ? Material.QUARTZ_BLOCK : Material.POLISHED_ANDESITE);
                for (int h = 1; h <= 5; h++) {
                    WorldUtil.setBlock(world, dx, y + h, dz,
                            border && h <= 3 ? Material.BARRIER : Material.AIR);
                }
            }
        }

        WorldUtil.removeTextDisplays(world, -HALF, y, -HALF, HALF + 1, y + 8, HALF + 1);
        WorldUtil.spawnLabel(world, 0.5, y + 3.2, 6.5,
                "&6&lWELCOME\n&7Right-click the compass\n&7to choose a minigame");

        spawn = new Location(world, 0.5, y + 1, 0.5, 0f, 0f);
        world.setSpawnLocation(0, y + 1, 0);
    }

    public Location spawn() {
        return spawn.clone();
    }

    public void send(Player p) {
        p.teleport(spawn());
        prepare(p);
    }

    public void prepare(Player p) {
        PlayerPrep.reset(p);
        items.give(p, 4, items.compass(), MenuItems.COMPASS);
        p.getInventory().setHeldItemSlot(4);
    }
}
