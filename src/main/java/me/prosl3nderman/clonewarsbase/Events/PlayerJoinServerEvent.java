package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.Internal.APIs.LuckPermsAPI;
import me.prosl3nderman.clonewarsbase.Internal.APIs.SkinAPI;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.Battalion;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class PlayerJoinServerEvent implements Listener {

    private CloneHandler cloneHandler;
    private BattalionHandler battalionHandler;
    private MySQLDatabase mySQLDatabase;
    private LuckPermsAPI luckPermsAPI;
    private SkinAPI skinAPI;

    @Inject
    public PlayerJoinServerEvent(CloneHandler cloneHandler, BattalionHandler battalionHandler, MySQLDatabase mySQLDatabase, LuckPermsAPI luckPermsAPI, SkinAPI skinAPI) {
        this.cloneHandler = cloneHandler;
        this.battalionHandler = battalionHandler;
        this.mySQLDatabase = mySQLDatabase;
        this.luckPermsAPI = luckPermsAPI;
        this.skinAPI = skinAPI;
    }

    private HashMap<UUID, Battalion> playerBattalions = new HashMap<>();
    private HashMap<UUID, String> playerRanks = new HashMap<>();

    @EventHandler
    public void onPrePlayerJoinServerEvent(AsyncPlayerPreLoginEvent event) {
        Battalion battalion = mySQLDatabase.getPlayersBattalion(event.getName());
        String rank = luckPermsAPI.getPlayerRank(event.getUniqueId());
        if (battalion == null)
            battalion = battalionHandler.getBattalion(rank);
        playerBattalions.put(event.getUniqueId(), battalion);
        playerRanks.put(event.getUniqueId(), rank);
    }

    @EventHandler
    public void onPlayerJoinServerEvent(PlayerJoinEvent event) {
        event.setJoinMessage("");
        cloneHandler.loadClone(event.getPlayer());

        Battalion battalion = playerBattalions.get(event.getPlayer().getUniqueId());
        String rank = playerRanks.get(event.getPlayer().getUniqueId());
        skinAPI.applyCloneSkin(event.getPlayer(), battalion, rank);

        playerRanks.remove(event.getPlayer().getUniqueId());
        playerBattalions.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerSpawnEvent(PlayerSpawnLocationEvent event) {
        Battalion battalion = playerBattalions.get(event.getPlayer().getUniqueId());

        event.setSpawnLocation(battalion.getSpawnPoint());

    }
}
