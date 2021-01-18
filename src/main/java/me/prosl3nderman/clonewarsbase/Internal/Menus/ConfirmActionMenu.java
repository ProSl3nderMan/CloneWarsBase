package me.prosl3nderman.clonewarsbase.Internal.Menus;

import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class ConfirmActionMenu {

    private CloneHandler cloneHandler;
    private CloneWarsBase plugin;
    private MySQLDatabase mySQLDatabase;

    @Inject
    public ConfirmActionMenu(CloneHandler cloneHandler, CloneWarsBase plugin, MySQLDatabase mySQLDatabase) {
        this.cloneHandler = cloneHandler;
        this.plugin = plugin;
        this.mySQLDatabase = mySQLDatabase;
    }

    private String menuTitle = ChatColor.GREEN + "Are you sure?";

    public String getMenuTitle() {
        return menuTitle;
    }

    public void openMenu(Clone clone, String action, String target) {
        Inventory inventory = Bukkit.createInventory(null, 9, menuTitle);

        ItemStack confirmYesGlass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
        ItemMeta meta = confirmYesGlass.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm " + action);
        meta.setLore(Arrays.asList("Clone Affected: " + target));
        confirmYesGlass.setItemMeta(meta);
        inventory.setItem(3, confirmYesGlass);

        ItemStack cancelGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        meta = cancelGlass.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Cancel " + action);
        cancelGlass.setItemMeta(meta);
        inventory.setItem(5, cancelGlass);

        clone.getPlayer().openInventory(inventory);
    }

    public void menuClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle() == null)
            return;
        if (event.getCurrentItem() == null)
            return;
        String invTitle = event.getView().getTitle();
        if (!invTitle.equalsIgnoreCase(menuTitle))
            return;

        ItemStack item = event.getCurrentItem();
        if (item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
            String targetPlayerName = ChatColor.stripColor(item.getItemMeta().getLore().get(0).split(": ")[1]);
            Player targetClonePlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetClonePlayer == null || !targetClonePlayer.isOnline()) { //offline clone kick
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        mySQLDatabase.removePlayerFromBattalion(ChatColor.stripColor(item.getItemMeta().getLore().get(0).split(": ")[1]));
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                Clone clone = cloneHandler.getClone(event.getWhoClicked().getUniqueId());
                                clone.getBattalion().removeCloneFromBattalion(targetPlayerName, "has been kicked from the battalion!");
                            }
                        });
                    }
                });
            } else {
                Clone targetClone = cloneHandler.getClone(targetClonePlayer.getUniqueId());
                String action = ChatColor.stripColor(item.getItemMeta().getDisplayName().split(" ")[1]);

                if (action.equalsIgnoreCase("kicking")) {
                    targetClone.getBattalion().removeCloneFromBattalion(targetClone, "has been kicked from the battalion!");
                    targetClone.leftBattalion();
                } else if (action.equalsIgnoreCase("leaving")) {
                    targetClone.getBattalion().removeCloneFromBattalion(targetClone, "has left the battalion!");
                    targetClone.leftBattalion();
                }
            }
        }
        event.getWhoClicked().closeInventory();
    }
}
