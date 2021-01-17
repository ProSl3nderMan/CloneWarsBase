package me.prosl3nderman.clonewarsbase.Internal.Clone;

import com.google.inject.Injector;
import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Handler;
import me.prosl3nderman.clonewarsbase.Internal.APIs.LuckPermsAPI;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class CloneHandler implements Handler {

    private Injector injector;
    private CloneWarsBase plugin;
    private LuckPermsAPI luckPermsAPI;
    private MySQLDatabase mySQLDatabase;

    @Inject
    public CloneHandler(Injector injector, CloneWarsBase plugin, LuckPermsAPI luckPermsAPI, MySQLDatabase mySQLDatabase) {
        this.injector = injector;
        this.plugin = plugin;
        this.luckPermsAPI = luckPermsAPI;
        this.mySQLDatabase = mySQLDatabase;
    }

    private HashMap<UUID, Clone> clones;

    @Override
    public void enable() {
        clones = new HashMap<>();
    }

    @Override
    public void disable() {
        clones.clear();
    }

    public Clone loadClone(Player player) {
        Clone clone = injector.getInstance(Clone.class);
        clone.loadPlayerVariables(player);

        clones.put(player.getUniqueId(), clone);

        return clone;
    }

    public Clone getClone(UUID playerUUID) {
        return clones.containsKey(playerUUID) ? clones.get(playerUUID) : null;
    }

    public void cloneWentOffline(UUID playerUUID) {
        if (clones.containsKey(playerUUID))
            clones.remove(playerUUID);
    }

    public void cloneRankup(String cloneName, String rank) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                UUID offlineCloneUUID = mySQLDatabase.getPlayerUUIDFromIGN(cloneName);
                String newRank = rank;
                if (rank.equalsIgnoreCase("null"))
                    newRank = luckPermsAPI.promotePlayerOnTrack(offlineCloneUUID);
                else
                    luckPermsAPI.setPlayerRank(offlineCloneUUID, rank);
                String newRankk = newRank;
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Clone clone = getClone(offlineCloneUUID);
                        if (clone != null) {
                            clone.updateRank(newRankk);
                            clone.getBattalion().cloneRankedUp(clone, newRankk);
                        }
                    }
                }, 20L);
            }
        });
    }
    public void cloneForceRankup(String cloneName, String rank) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                UUID offlineCloneUUID = mySQLDatabase.getPlayerUUIDFromIGN(cloneName);
                String newRank = rank;
                if (rank.equalsIgnoreCase("null"))
                    newRank = luckPermsAPI.promotePlayerOnTrack(offlineCloneUUID);
                else
                    luckPermsAPI.setPlayerRank(offlineCloneUUID, rank);
                Clone clone = getClone(offlineCloneUUID);
                String newRankk = newRank;
                if (clone != null) {
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            clone.updateRank(newRankk);
                            clone.getBattalion().cloneRankedUp(clone, newRankk);
                        }
                    }, 20L);
                }
            }
        });
    }

    public void cloneDerank(String cloneName, String rank) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                UUID offlineCloneUUID = mySQLDatabase.getPlayerUUIDFromIGN(cloneName);
                String newRank = rank;
                if (rank.equalsIgnoreCase("null"))
                    newRank = luckPermsAPI.demotePlayerOnTrack(offlineCloneUUID);
                else
                    luckPermsAPI.setPlayerRank(offlineCloneUUID, rank);
                String newRankk = newRank;
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Clone clone = getClone(offlineCloneUUID);
                        if (clone != null) {
                            clone.updateRank(newRankk);
                            clone.sendMessage(ChatColor.GREEN + "You have been demoted to " + ChatColor.WHITE + clone.getRank());
                        }
                    }
                }, 20L);
            }
        });
    }
}
