package com.zartex.tycoon.plot;

import com.zartex.tycoon.config.Settings;
import com.zartex.tycoon.model.MachineType;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.util.Text;
import com.zartex.tycoon.util.WorldUtil;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;

public class PlotManager {

    public static final int PLOT_SIZE = 21;
    public static final int PLOT_SPACING = 32;
    private static final int PAD_Z = 10;
    private static final int PAD_START = 2;
    private static final int PAD_STEP = 4;
    private static final int FIRST_ROW_Z = 24;
    private static final int SPAWN_HALF_WIDTH = 15;
    private static final int SPAWN_DEPTH = 21;

    private final WorldService worlds;
    private final Settings settings;
    private World world;
    private int groundY;
    private int columns;
    private int count;
    private int spawnX;

    public PlotManager(WorldService worlds, Settings settings) {
        this.worlds = worlds;
        this.settings = settings;
    }

    /** Builds the spawn platform and every (empty) plot. */
    public void init() {
        world = worlds.tycoon();
        count = settings.plotCount();
        columns = Math.min(settings.plotColumns(), count);
        groundY = world.getHighestBlockYAt(-5, -5);

        int rows = (count + columns - 1) / columns;
        int width = columns * PLOT_SPACING;
        int depth = FIRST_ROW_Z + rows * PLOT_SPACING;
        spawnX = (width - (PLOT_SPACING - PLOT_SIZE)) / 2;

        WorldUtil.forceLoad(world, spawnX - SPAWN_HALF_WIDTH - 1, -1,
                spawnX + SPAWN_HALF_WIDTH + 1, SPAWN_DEPTH + 1);
        WorldUtil.forceLoad(world, -1, FIRST_ROW_Z - 1, width, depth);

        buildSpawn();
        for (int i = 0; i < count; i++) release(i);

        WorldBorder border = world.getWorldBorder();
        border.setCenter(width / 2.0, depth / 2.0);
        border.setSize(Math.max(width, depth) + 80);
        world.setSpawnLocation(spawnX, groundY + 1, 6);
    }

    public int count() {
        return count;
    }

    public Location spawn() {
        return new Location(world, spawnX + 0.5, groundY + 1, 6.5, 0f, 0f);
    }

    public Location home(int plot) {
        return new Location(world, originX(plot) + 10.5, groundY + 1, originZ(plot) + 3.5, 0f, 0f);
    }

    private int originX(int plot) {
        return (plot % columns) * PLOT_SPACING;
    }

    private int originZ(int plot) {
        return FIRST_ROW_Z + (plot / columns) * PLOT_SPACING;
    }

    /** Shows the player's machines and name on a plot. */
    public void claim(int plot, String ownerName, PlayerData d) {
        for (MachineType type : MachineType.values()) {
            renderMachine(plot, type, d.owns(type));
        }
        setLabel(plot, "&6" + ownerName + "&e's plot\n&7Plot #" + (plot + 1));
    }

    /** Empties a plot and marks it as free. */
    public void release(int plot) {
        buildFloor(plot);
        for (MachineType type : MachineType.values()) {
            int px = originX(plot) + PAD_START + type.index() * PAD_STEP;
            int pz = originZ(plot) + PAD_Z;
            for (int h = 1; h <= 3; h++) {
                WorldUtil.setBlock(world, px, groundY + h, pz, Material.AIR);
            }
            WorldUtil.removeTextDisplays(world, px, groundY, pz, px + 1, groundY + 5, pz + 1);
        }
        setLabel(plot, "&7Plot #" + (plot + 1) + "\n&aFree");
    }

    public void renderMachine(int plot, MachineType type, boolean owned) {
        int px = originX(plot) + PAD_START + type.index() * PAD_STEP;
        int pz = originZ(plot) + PAD_Z;

        if (owned) {
            for (int h = 0; h < 3; h++) {
                WorldUtil.setBlock(world, px, groundY + h, pz, type.getBlock());
            }
        } else {
            WorldUtil.setBlock(world, px, groundY, pz, Material.RED_CONCRETE);
            WorldUtil.setBlock(world, px, groundY + 1, pz, Material.STONE_PRESSURE_PLATE);
            WorldUtil.setBlock(world, px, groundY + 2, pz, Material.AIR);
        }

        WorldUtil.removeTextDisplays(world, px, groundY, pz, px + 1, groundY + 5, pz + 1);
        String label = owned
                ? type.getColor() + type.getDisplayName() + "\n&a+$" + type.getIncome() + "/s"
                : type.getColor() + type.getDisplayName()
                  + "\n&7Price: &e$" + Text.money(type.getPrice())
                  + "\n&a+$" + type.getIncome() + "/s";
        WorldUtil.spawnLabel(world, px + 0.5, owned ? groundY + 3.4 : groundY + 1.7, pz + 0.5, label);
    }

    /** Returns the pad at this block, or null if the block is not a buy pad. */
    public PadInfo findPad(Block b) {
        if (!worlds.isTycoon(b.getWorld())) return null;
        if (b.getType() != Material.STONE_PRESSURE_PLATE || b.getY() != groundY + 1) return null;

        int col = Math.floorDiv(b.getX(), PLOT_SPACING);
        int relX = b.getX() - col * PLOT_SPACING - PAD_START;
        int rz = b.getZ() - FIRST_ROW_Z;
        int row = Math.floorDiv(rz, PLOT_SPACING);
        int relZ = rz - row * PLOT_SPACING;

        if (col < 0 || col >= columns || row < 0 || relZ != PAD_Z) return null;
        if (relX < 0 || relX % PAD_STEP != 0) return null;

        int index = relX / PAD_STEP;
        int plot = row * columns + col;
        if (index >= MachineType.count() || plot >= count) return null;
        return new PadInfo(plot, MachineType.fromIndex(index));
    }

    private void buildFloor(int plot) {
        int ox = originX(plot);
        int oz = originZ(plot);
        for (int dx = 0; dx < PLOT_SIZE; dx++) {
            for (int dz = 0; dz < PLOT_SIZE; dz++) {
                boolean border = dx == 0 || dz == 0 || dx == PLOT_SIZE - 1 || dz == PLOT_SIZE - 1;
                WorldUtil.setBlock(world, ox + dx, groundY, oz + dz,
                        border ? Material.QUARTZ_BLOCK : Material.POLISHED_ANDESITE);
            }
        }
    }

    private void setLabel(int plot, String text) {
        double x = originX(plot) + 10.5;
        double y = groundY + 3.2;
        double z = originZ(plot) + 0.5;
        WorldUtil.removeTextDisplays(world, x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
        WorldUtil.spawnLabel(world, x, y, z, text);
    }

    private void buildSpawn() {
        int x1 = spawnX - SPAWN_HALF_WIDTH;
        int x2 = spawnX + SPAWN_HALF_WIDTH;
        for (int x = x1; x <= x2; x++) {
            for (int z = 0; z < SPAWN_DEPTH; z++) {
                boolean border = x == x1 || x == x2 || z == 0 || z == SPAWN_DEPTH - 1;
                WorldUtil.setBlock(world, x, groundY, z,
                        border ? Material.QUARTZ_BLOCK : Material.STONE_BRICKS);
                for (int h = 1; h <= 6; h++) {
                    WorldUtil.setBlock(world, x, groundY + h, z, Material.AIR);
                }
            }
        }
        WorldUtil.removeTextDisplays(world, x1, groundY, 0, x2 + 1, groundY + 8, SPAWN_DEPTH);
        WorldUtil.spawnLabel(world, spawnX + 0.5, groundY + 3.2, 14.5,
                "&6&lTYCOON\n&7Type &e/create tycoon\n&7to get your own plot");
    }
}
