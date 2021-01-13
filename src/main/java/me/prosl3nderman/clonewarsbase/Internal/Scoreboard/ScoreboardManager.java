package me.prosl3nderman.clonewarsbase.Internal.Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.inject.Singleton;

@Singleton
public class ScoreboardManager {

    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective titleObj = scoreboard.registerNewObjective("rankBelowName", "", "");
    private Objective tabObj = scoreboard.registerNewObjective("rankTab", "", "");

    private Scoreboard getPlayerScoreboard(Player player) {
//        if (player.getScoreboard() == null)
            return scoreboard;
        //return player.getScoreboard();
    }

    public void setPlayersBelowNameTitle(String title, Player player) {
        titleObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        titleObj.setDisplayName(title);
        player.setScoreboard(scoreboard);
    }

    public void setPlayersTabTitle(String title, Player player) {
        tabObj.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        tabObj.setDisplayName(title);
        player.setScoreboard(scoreboard);
    }
}
