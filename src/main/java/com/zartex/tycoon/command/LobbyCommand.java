package com.zartex.tycoon.command;

import com.zartex.tycoon.lobby.LobbyManager;
import com.zartex.tycoon.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand implements CommandExecutor {

    private final LobbyManager lobby;

    public LobbyCommand(LobbyManager lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        lobby.send(p);
        p.sendMessage(Text.color("&aBack at the lobby."));
        return true;
    }
}
