package com.zartex.tycoon.model;

import java.util.Arrays;

public class PlayerData {

    private long money;
    private final int plot;
    private final boolean[] owned = new boolean[MachineType.count()];

    public PlayerData(int plot) {
        this.plot = plot;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void addMoney(long amount) {
        this.money += amount;
    }

    public int getPlot() {
        return plot;
    }

    public boolean owns(MachineType type) {
        return owned[type.index()];
    }

    public void setOwned(MachineType type, boolean value) {
        owned[type.index()] = value;
    }

    public long income() {
        long total = 0;
        for (MachineType type : MachineType.values()) {
            if (owns(type)) total += type.getIncome();
        }
        return total;
    }

    public void reset() {
        money = 0;
        Arrays.fill(owned, false);
        setOwned(MachineType.COPPER, true);
    }
}
