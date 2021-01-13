package me.prosl3nderman.clonewarsbase.Internal.Player;

import me.prosl3nderman.clonewarsbase.Internal.Battalions.Battalion;
import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.LuckPermsAPI;
import me.prosl3nderman.clonewarsbase.Internal.Scoreboard.ScoreboardManager;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import java.util.UUID;

public class Player {

    private CloneWarsBase plugin;
    private LuckPermsAPI luckPermsAPI;
    private MySQLDatabase mySQLDatabase;
    private ScoreboardManager scoreboardManager;

    @Inject
    public Player(CloneWarsBase plugin, LuckPermsAPI luckPermsAPI, MySQLDatabase mySQLDatabase, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.luckPermsAPI = luckPermsAPI;
        this.mySQLDatabase = mySQLDatabase;
        this.scoreboardManager = scoreboardManager;
    }

    private UUID playerUUID;
    private String playerName;
    private Integer cloneID;
    private String rankName;
    private String rank;
    private Battalion battalion;

    public void loadPlayerVariables(org.bukkit.entity.Player bukkitPlayer) {
        playerUUID = bukkitPlayer.getUniqueId();
        playerName = bukkitPlayer.getName();
        Player player = this;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                cloneID = mySQLDatabase.updatePlayerInformationAndGetPlayerID(player);
                rankName = luckPermsAPI.getPlayerRankPrefix(playerUUID);
                rank = luckPermsAPI.getPlayerRank(playerUUID);
                battalion = mySQLDatabase.getPlayersBattalion(player);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        scoreboardManager.setPlayersBelowNameTitle(getRankTitle(), getPlayer());
                        scoreboardManager.setPlayersTabTitle(getRankTitle() + playerName, getPlayer());
                        battalion.battalionPlayerCameOnline(player);
                    }
                });
            }
        });
    }

    public void playerWentOffline() {
        battalion.battalionPlayerWentOffline(this);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public String getPlayerName() { return playerName; }

    public org.bukkit.entity.Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public String getRankName() {
        return rankName;
    }

    public String getRank() {
        return rank;
    }

    public String getRankTitle() {
        return battalion.getBattalionName().equalsIgnoreCase("ct")
                ?
                battalion.getBattalionColor() + rankName + " " + cloneID
                :
                battalion.getBattalionColor() + battalion.getAbbreviatedBattalionName() + " " + rankName;
    }
}
