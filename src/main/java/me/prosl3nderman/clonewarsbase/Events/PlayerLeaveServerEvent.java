package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.Internal.Player.PlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerLeaveServerEvent implements Listener {

    private PlayerHandler playerHandler;

    @Inject
    public PlayerLeaveServerEvent(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @EventHandler
    public void onPlayerLeaveServer(PlayerQuitEvent event) {
        playerHandler.getPlayer(event.getPlayer().getUniqueId()).playerWentOffline();
    }
}
