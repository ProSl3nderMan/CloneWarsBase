package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.Internal.Chat.ChatHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AsyncChatEvent implements Listener {

    private ChatHandler chatHandler;

    @Inject
    public AsyncChatEvent(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @EventHandler
    public void onAsyncChatEvent(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        chatHandler.handleChatMessage(event.getPlayer().getUniqueId(), event.getMessage());
    }

}
