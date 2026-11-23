package com.zartex.tycoon.model;

import org.bukkit.Material;

public enum MachineType {
    COPPER("Copper Machine", "&6", 0, 1, Material.COPPER_BLOCK),
    IRON("Iron Machine", "&f", 100, 3, Material.IRON_BLOCK),
    GOLD("Gold Machine", "&e", 500, 10, Material.GOLD_BLOCK),
    DIAMOND("Diamond Machine", "&b", 2500, 40, Material.DIAMOND_BLOCK),
    EMERALD("Emerald Machine", "&a", 10000, 150, Material.EMERALD_BLOCK);

    private final String displayName;
    private final String color;
    private final long price;
    private final long income;
    private final Material block;

    MachineType(String displayName, String color, long price, long income, Material block) {
        this.displayName = displayName;
        this.color = color;
        this.price = price;
        this.income = income;
        this.block = block;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public long getPrice() {
        return price;
    }

    public long getIncome() {
        return income;
    }

    public Material getBlock() {
        return block;
    }

    public int index() {
        return ordinal();
    }

    public MachineType previous() {
        return ordinal() == 0 ? null : values()[ordinal() - 1];
    }

    public static int count() {
        return values().length;
    }

    public static MachineType fromIndex(int index) {
        return values()[index];
    }
}
