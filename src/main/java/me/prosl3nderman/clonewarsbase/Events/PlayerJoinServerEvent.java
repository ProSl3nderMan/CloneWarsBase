package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerJoinServerEvent implements Listener {

    private CloneHandler cloneHandler;

    @Inject
    public PlayerJoinServerEvent(CloneHandler cloneHandler) {
        this.cloneHandler = cloneHandler;
    }

    @EventHandler
    public void onPlayerJoinServerEvent(PlayerJoinEvent event) {
        cloneHandler.loadClone(event.getPlayer());
    }
}
