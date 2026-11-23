package com.zartex.tycoon.command;

import com.zartex.tycoon.board.BoardManager;
import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.MachineType;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PlotManager;
import com.zartex.tycoon.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TycoonCommand implements CommandExecutor {

    private final DataManager data;
    private final PlotManager plots;
    private final BoardManager boards;

    public TycoonCommand(DataManager data, PlotManager plots, BoardManager boards) {
        this.data = data;
        this.plots = plots;
        this.boards = boards;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        PlayerData d = data.get(p.getUniqueId());
        if (d == null) return true;

        if (args.length == 0 || args[0].equalsIgnoreCase("home")) {
            p.teleport(plots.home(d.getPlot()));
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            handleReset(p, d, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("give") && p.isOp() && args.length >= 2) {
            handleGive(p, d, args[1]);
            return true;
        }

        p.sendMessage(Text.color("&eUsage: /tycoon [home|reset]"));
        return true;
    }

    private void handleReset(Player p, PlayerData d, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            p.sendMessage(Text.color("&cThis erases your money and machines. Type &e/tycoon reset confirm"));
            return;
        }
        d.reset();
        for (MachineType type : MachineType.values()) {
            plots.renderMachine(d.getPlot(), type, d.owns(type));
        }
        boards.update(p);
        p.sendMessage(Text.color("&aYour tycoon was reset."));
    }

    private void handleGive(Player p, PlayerData d, String amount) {
        try {
            d.addMoney(Long.parseLong(amount));
            boards.update(p);
            p.sendMessage(Text.color("&aMoney added."));
        } catch (NumberFormatException ex) {
            p.sendMessage(Text.color("&cInvalid number."));
        }
    }
}
