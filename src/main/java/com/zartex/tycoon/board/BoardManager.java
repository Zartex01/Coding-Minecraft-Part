package com.zartex.tycoon.board;

import com.zartex.tycoon.data.DataManager;
import com.zartex.tycoon.model.PlayerData;
import com.zartex.tycoon.plot.PlotRegistry;
import com.zartex.tycoon.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoardManager {

    private final DataManager data;
    private final PlotRegistry registry;
    private final Map<UUID, Scoreboard> boards = new HashMap<>();

    public BoardManager(DataManager data, PlotRegistry registry) {
        this.data = data;
        this.registry = registry;
    }

    public void setup(Player p) {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective o = sb.registerNewObjective("tycoon", Criteria.DUMMY,
                Component.text("TYCOON", NamedTextColor.GOLD));
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        boards.put(p.getUniqueId(), sb);
        p.setScoreboard(sb);
        update(p);
    }

    public void update(Player p) {
        Scoreboard sb = boards.get(p.getUniqueId());
        PlayerData d = data.get(p.getUniqueId());
        if (sb == null || d == null) return;
        Objective o = sb.getObjective("tycoon");
        if (o == null) return;

        for (String entry : new ArrayList<>(sb.getEntries())) {
            sb.resetScores(entry);
        }
        int plot = registry.plotOf(p.getUniqueId()) + 1;
        o.getScore(Text.color("&eMoney: &f$" + Text.money(d.getMoney()))).setScore(3);
        o.getScore(Text.color("&aIncome: &f$" + Text.money(d.income()) + "/s")).setScore(2);
        o.getScore(Text.color("&7Plot #" + plot)).setScore(1);
    }

    public void remove(Player p) {
        if (boards.remove(p.getUniqueId()) != null) {
            p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
}
