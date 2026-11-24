package com.zartex.tycoon.listener;

import com.zartex.tycoon.game.TycoonGame;
import com.zartex.tycoon.lobby.LobbyManager;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener implements Listener {

    private final WorldService worlds;
    private final LobbyManager lobby;
    private final TycoonGame game;

    public WorldChangeListener(WorldService worlds, LobbyManager lobby, TycoonGame game) {
        this.worlds = worlds;
        this.lobby = lobby;
        this.game = game;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (worlds.isTycoon(e.getFrom())) game.leave(p);

        if (worlds.isLobby(p.getWorld())) {
            lobby.prepare(p);
        } else if (worlds.isTycoon(p.getWorld())) {
            game.prepareSpawn(p);
        }
    }
}
