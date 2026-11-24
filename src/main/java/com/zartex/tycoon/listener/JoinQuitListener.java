package com.zartex.tycoon.listener;

import com.zartex.tycoon.game.TycoonGame;
import com.zartex.tycoon.lobby.LobbyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinQuitListener implements Listener {

    private final JavaPlugin plugin;
    private final LobbyManager lobby;
    private final TycoonGame game;

    public JoinQuitListener(JavaPlugin plugin, LobbyManager lobby, TycoonGame game) {
        this.plugin = plugin;
        this.lobby = lobby;
        this.game = game;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (p.isOnline()) lobby.send(p);
        }, 2L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        game.leave(e.getPlayer());
    }
}
