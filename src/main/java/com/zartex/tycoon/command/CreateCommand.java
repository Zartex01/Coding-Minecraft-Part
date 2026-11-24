package com.zartex.tycoon.command;

import com.zartex.tycoon.game.TycoonGame;
import com.zartex.tycoon.util.Text;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommand implements TabExecutor {

    private final WorldService worlds;
    private final TycoonGame game;

    public CreateCommand(WorldService worlds, TycoonGame game) {
        this.worlds = worlds;
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (args.length == 0 || !args[0].equalsIgnoreCase("tycoon")) {
            p.sendMessage(Text.color("&eUsage: /create tycoon"));
            return true;
        }
        if (!worlds.isTycoon(p.getWorld())) {
            p.sendMessage(Text.color("&cThis command only works in the Tycoon world. Pick Tycoon in the lobby menu."));
            return true;
        }

        switch (game.create(p)) {
            case CREATED -> p.sendMessage(Text.color("&aYour tycoon is ready on plot #"
                    + game.plotNumber(p) + "! &7Step on the red pads to buy machines."));
            case ALREADY_HAS_PLOT -> p.sendMessage(Text.color("&eYou already have a plot. Teleporting you back."));
            case FULL -> p.sendMessage(Text.color("&cAll plots are taken right now. Try again in a few minutes."));
            case WRONG_WORLD -> p.sendMessage(Text.color("&cThis command only works in the Tycoon world."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) return List.of("tycoon");
        return List.of();
    }
}
