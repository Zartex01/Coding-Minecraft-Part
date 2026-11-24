package com.zartex.tycoon.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.BoundingBox;

public final class WorldUtil {

    private WorldUtil() {
    }

    public static void setBlock(World world, int x, int y, int z, Material type) {
        world.getBlockAt(x, y, z).setType(type, false);
    }

    /** Keeps the chunks of a block area loaded so labels and blocks stay reliable. */
    public static void forceLoad(World world, int minX, int minZ, int maxX, int maxZ) {
        for (int cx = Math.floorDiv(minX, 16); cx <= Math.floorDiv(maxX, 16); cx++) {
            for (int cz = Math.floorDiv(minZ, 16); cz <= Math.floorDiv(maxZ, 16); cz++) {
                world.setChunkForceLoaded(cx, cz, true);
                world.getChunkAt(cx, cz);
            }
        }
    }

    public static void removeTextDisplays(World world, double x1, double y1, double z1,
                                          double x2, double y2, double z2) {
        BoundingBox box = new BoundingBox(x1, y1, z1, x2, y2, z2);
        for (Entity entity : world.getNearbyEntities(box, e -> e instanceof TextDisplay)) {
            entity.remove();
        }
    }

    public static void spawnLabel(World world, double x, double y, double z, String text) {
        world.spawn(new Location(world, x, y, z), TextDisplay.class, td -> {
            td.text(Text.component(text));
            td.setBillboard(Display.Billboard.CENTER);
            td.setPersistent(false);
        });
    }
}
