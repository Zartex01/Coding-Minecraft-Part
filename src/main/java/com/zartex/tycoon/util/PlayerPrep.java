package com.zartex.tycoon.util;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class PlayerPrep {

    private PlayerPrep() {
    }

    /** Puts a player in minigame state. Operators keep their inventory and game mode. */
    public static void reset(Player p) {
        if (!p.isOp()) {
            p.setGameMode(GameMode.ADVENTURE);
            p.getInventory().clear();
        }
        p.setFoodLevel(20);
        p.setFireTicks(0);
    }
}
