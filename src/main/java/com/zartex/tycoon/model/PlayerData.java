package com.zartex.tycoon.model;

import java.util.Arrays;

public class PlayerData {

    private long money;
    private final boolean[] owned = new boolean[MachineType.count()];

    public PlayerData() {
        owned[MachineType.COPPER.index()] = true;
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
        owned[MachineType.COPPER.index()] = true;
    }
}
