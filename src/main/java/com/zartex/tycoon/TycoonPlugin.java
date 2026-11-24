package com.zartex.tycoon;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.command.CreateCommand;
import com.zartex.tycoon.command.GamesCommand;
import com.zartex.tycoon.command.LobbyCommand;
import com.zartex.tycoon.command.TycoonCommand;
import com.zartex.tycoon.config.Settings;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.game.TycoonGame;
import com.zartex.tycoon.item.MenuItems;
import com.zartex.tycoon.listener.JoinQuitListener;
import com.zartex.tycoon.listener.MenuClickListener;
import com.zartex.tycoon.listener.MenuItemListener;
import com.zartex.tycoon.listener.PadListener;
import com.zartex.tycoon.listener.ProtectionListener;
import com.zartex.tycoon.listener.WorldChangeListener;
import com.zartex.tycoon.lobby.LobbyManager;
import com.zartex.tycoon.lobby.MinigameMenu;
import com.zartex.tycoon.plot.PlotManager;
import com.zartex.tycoon.plot.PlotRegistry;
import com.zartex.tycoon.shop.MachineShop;
import com.zartex.tycoon.task.IncomeTask;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TycoonPlugin extends JavaPlugin {

    private DataManager data;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Settings settings = new Settings(this);

        WorldService worlds = new WorldService(settings);
        if (!worlds.init()) {
            getLogger().severe("Could not load the lobby and tycoon worlds. Check config.yml.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        data = new DataManager(this);
        data.load();

        MenuItems items = new MenuItems(this);

        LobbyManager lobby = new LobbyManager(worlds, items);
        lobby.init();

        PlotManager plots = new PlotManager(worlds, settings);
        plots.init();

        PlotRegistry registry = new PlotRegistry(plots.count());
        BoardManager boards = new BoardManager(data, registry);
        TycoonGame game = new TycoonGame(worlds, data, plots, registry, boards, items);
        MachineShop shop = new MachineShop(data, registry, plots, boards);
        MinigameMenu menu = new MinigameMenu(game);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitListener(this, lobby, game), this);
        pm.registerEvents(new WorldChangeListener(worlds, lobby, game), this);
        pm.registerEvents(new MenuItemListener(items, menu, lobby), this);
        pm.registerEvents(new MenuClickListener(game), this);
        pm.registerEvents(new PadListener(plots, shop), this);
        pm.registerEvents(new ProtectionListener(worlds, lobby, game), this);

        register("create", new CreateCommand(worlds, game));
        register("lobby", new LobbyCommand(lobby));
        register("games", new GamesCommand(menu));
        register("tycoon", new TycoonCommand(game, worlds));

        Bukkit.getScheduler().runTaskTimer(this, new IncomeTask(worlds, data, registry, boards), 20L, 20L);
        long saveTicks = settings.autosaveSeconds() * 20L;
        Bukkit.getScheduler().runTaskTimer(this, data::saveAsync, saveTicks, saveTicks);
    }

    @Override
    public void onDisable() {
        if (data != null) data.saveNow();
    }

    private void register(String name, CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command != null) command.setExecutor(executor);
    }
}
