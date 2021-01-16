package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerLeaveServerEvent implements Listener {

    private CloneHandler cloneHandler;

    @Inject
    public PlayerLeaveServerEvent(CloneHandler cloneHandler) {
        this.cloneHandler = cloneHandler;
    }

    @EventHandler
    public void onPlayerLeaveServer(PlayerQuitEvent event) {
        cloneHandler.getClone(event.getPlayer().getUniqueId()).wentOffline();
    }
}
