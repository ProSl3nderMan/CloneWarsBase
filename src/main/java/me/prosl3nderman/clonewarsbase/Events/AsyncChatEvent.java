package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Chat.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AsyncChatEvent implements Listener {

    private CloneWarsBase plugin;
    private ChatHandler chatHandler;

    @Inject
    public AsyncChatEvent(CloneWarsBase plugin, ChatHandler chatHandler) {
        this.plugin = plugin;
        this.chatHandler = chatHandler;
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                chatHandler.handleChatMessage(event.getPlayer().getUniqueId(), event.getMessage());
            }
        });
    }

}
