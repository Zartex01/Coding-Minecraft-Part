package com.zartex.tycoon.plot;

import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.MachineType;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

public class PlotManager {

    public static final int PLOT_SIZE = 21;
    public static final int PLOT_SPACING = 32;
    private static final int PAD_Z = 10;
    private static final int PAD_START = 2;
    private static final int PAD_STEP = 4;

    private final DataManager data;
    private World world;
    private int groundY;

    public PlotManager(DataManager data) {
        this.data = data;
    }

    /** Loads or creates the tycoon world. Returns false if it fails. */
    public boolean init() {
        world = Bukkit.getWorld("tycoon");
        if (world == null) {
            world = new WorldCreator("tycoon")
                    .type(WorldType.FLAT)
                    .generateStructures(false)
                    .createWorld();
        }
        if (world == null) return false;

        groundY = data.hasGroundY() ? data.getGroundY() : world.getHighestBlockYAt(0, 0);
        data.setGroundY(groundY);
        world.setSpawnLocation(10, groundY + 1, 3);
        return true;
    }

    public boolean isTycoonWorld(World w) {
        return w != null && w.equals(world);
    }

    public World getWorld() {
        return world;
    }

    public Location home(int plot) {
        return new Location(world, plot * PLOT_SPACING + 10.5, groundY + 1, 3.5, 0f, 0f);
    }

    public void keepDaytime() {
        world.setTime(6000);
        world.setStorm(false);
    }

    public void buildPlot(PlayerData d) {
        int ox = d.getPlot() * PLOT_SPACING;
        for (int dx = 0; dx < PLOT_SIZE; dx++) {
            for (int dz = 0; dz < PLOT_SIZE; dz++) {
                boolean border = dx == 0 || dz == 0 || dx == PLOT_SIZE - 1 || dz == PLOT_SIZE - 1;
                world.getBlockAt(ox + dx, groundY, dz)
                        .setType(border ? Material.QUARTZ_BLOCK : Material.POLISHED_ANDESITE);
            }
        }
        for (MachineType type : MachineType.values()) {
            renderMachine(d.getPlot(), type, d.owns(type));
        }
    }

    public void renderMachine(int plot, MachineType type, boolean owned) {
        int px = plot * PLOT_SPACING + PAD_START + type.index() * PAD_STEP;
        int pz = PAD_Z;

        if (owned) {
            for (int h = 0; h < 3; h++) {
                world.getBlockAt(px, groundY + h, pz).setType(type.getBlock());
            }
        } else {
            world.getBlockAt(px, groundY, pz).setType(Material.RED_CONCRETE);
            world.getBlockAt(px, groundY + 1, pz).setType(Material.STONE_PRESSURE_PLATE);
            world.getBlockAt(px, groundY + 2, pz).setType(Material.AIR);
        }

        Location center = new Location(world, px + 0.5, groundY + 2, pz + 0.5);
        for (Entity old : world.getNearbyEntities(center, 1.0, 3.0, 1.0, ent -> ent instanceof TextDisplay)) {
            old.remove();
        }

        String label = owned
                ? type.getColor() + type.getDisplayName() + "\n&a+$" + type.getIncome() + "/s"
                : type.getColor() + type.getDisplayName()
                  + "\n&7Price: &e$" + Text.money(type.getPrice())
                  + "\n&a+$" + type.getIncome() + "/s";
        Location labelLoc = new Location(world, px + 0.5, owned ? groundY + 3.4 : groundY + 1.7, pz + 0.5);
        world.spawn(labelLoc, TextDisplay.class, td -> {
            td.text(Text.component(label));
            td.setBillboard(Display.Billboard.CENTER);
        });
    }

    /** Returns the pad at this block, or null if the block is not a buy pad. */
    public PadInfo findPad(Block b) {
        if (!isTycoonWorld(b.getWorld()) || b.getType() != Material.STONE_PRESSURE_PLATE) return null;
        if (b.getZ() != PAD_Z || b.getY() != groundY + 1) return null;

        int plot = Math.floorDiv(b.getX(), PLOT_SPACING);
        int rel = b.getX() - plot * PLOT_SPACING - PAD_START;
        if (rel < 0 || rel % PAD_STEP != 0) return null;

        int index = rel / PAD_STEP;
        if (index >= MachineType.count()) return null;
        return new PadInfo(plot, MachineType.fromIndex(index));
    }
}
