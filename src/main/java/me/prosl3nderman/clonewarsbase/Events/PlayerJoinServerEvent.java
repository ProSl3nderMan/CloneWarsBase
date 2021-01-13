package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.Internal.Player.PlayerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerJoinServerEvent implements Listener {

    private PlayerHandler playerHandler;

    @Inject
    public PlayerJoinServerEvent(PlayerHandler playerHandler) {
        this.playerHandler = playerHandler;
    }

    @EventHandler
    public void onPlayerJoinServerEvent(PlayerJoinEvent event) {
        playerHandler.loadPlayer(event.getPlayer());
    }
}
