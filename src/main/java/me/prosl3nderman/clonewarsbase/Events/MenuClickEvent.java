package me.prosl3nderman.clonewarsbase.Events;

import me.prosl3nderman.clonewarsbase.Internal.Menus.ConfirmActionMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MenuClickEvent implements Listener {

    private ConfirmActionMenu confirmActionMenu;

    @Inject
    public MenuClickEvent(ConfirmActionMenu confirmActionMenu) {
        this.confirmActionMenu = confirmActionMenu;
    }

    @EventHandler
    public void onMovePlayerItemsWhileInShop(InventoryClickEvent event) {
        confirmActionMenu.menuClickEvent(event);
    }

}
