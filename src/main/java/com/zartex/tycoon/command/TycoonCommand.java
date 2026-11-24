package com.zartex.tycoon.command;

import com.zartex.tycoon.game.TycoonGame;
import com.zartex.tycoon.util.Text;
import com.zartex.tycoon.world.WorldService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TycoonCommand implements CommandExecutor {

    private final TycoonGame game;
    private final WorldService worlds;

    public TycoonCommand(TycoonGame game, WorldService worlds) {
        this.game = game;
        this.worlds = worlds;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (!worlds.isTycoon(p.getWorld())) {
            p.sendMessage(Text.color("&cThis command only works in the Tycoon world."));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("home")) {
            game.teleportHome(p);
            return true;
        }
        if (args[0].equalsIgnoreCase("reset")) {
            handleReset(p, args);
            return true;
        }
        if (args[0].equalsIgnoreCase("give") && p.isOp() && args.length >= 2) {
            handleGive(p, args[1]);
            return true;
        }

        p.sendMessage(Text.color("&eUsage: /tycoon [home|reset]"));
        return true;
    }

    private void handleReset(Player p, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            p.sendMessage(Text.color("&cThis erases your money and machines. Type &e/tycoon reset confirm"));
            return;
        }
        if (game.reset(p)) {
            p.sendMessage(Text.color("&aYour tycoon was reset."));
        } else {
            p.sendMessage(Text.color("&cCreate your tycoon first: &e/create tycoon"));
        }
    }

    private void handleGive(Player p, String amount) {
        try {
            if (game.giveMoney(p, Long.parseLong(amount))) {
                p.sendMessage(Text.color("&aMoney added."));
            } else {
                p.sendMessage(Text.color("&cCreate your tycoon first: &e/create tycoon"));
            }
        } catch (NumberFormatException ex) {
            p.sendMessage(Text.color("&cInvalid number."));
        }
    }
}
