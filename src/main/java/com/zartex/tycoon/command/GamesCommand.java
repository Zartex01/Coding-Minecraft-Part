package com.zartex.tycoon.command;

import com.zartex.tycoon.lobby.MinigameMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamesCommand implements CommandExecutor {

    private final MinigameMenu menu;

    public GamesCommand(MinigameMenu menu) {
        this.menu = menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        menu.open(p);
        return true;
    }
}
