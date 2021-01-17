package me.prosl3nderman.clonewarsbase.Internal.Chat;

import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import me.prosl3nderman.clonewarsbase.Internal.Handler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Singleton
public class ChatHandler implements Handler {

    private CloneWarsBase plugin;
    private CloneHandler cloneHandler;

    @Inject
    public ChatHandler(CloneWarsBase plugin, CloneHandler cloneHandler) {
        this.plugin = plugin;
        this.cloneHandler = cloneHandler;
    }


    private HashMap<Character, ChatMode> chatShortcuts = new HashMap<>();

    @Override
    public void enable() {
        chatShortcuts.put(plugin.getConfig().getString("chatShortcuts.ooc").charAt(0), ChatMode.OUT_OF_CHARACTER);
        chatShortcuts.put(plugin.getConfig().getString("chatShortcuts.staff").charAt(0), ChatMode.STAFF);
        chatShortcuts.put(plugin.getConfig().getString("chatShortcuts.officer").charAt(0), ChatMode.OFFICER);
    }

    @Override
    public void disable() {

    }

    public void onAsyncChatEvent(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        Clone clone = cloneHandler.getClone(e.getPlayer().getUniqueId());
        ChatMode chatMode = clone.getChatMode();

        String nameAndMessage = clone.getBattalion().getColoredAbbreviatedName() + " " + clone.getColoredRank() + " " + clone.getName() + ChatColor.WHITE + ": " + e.getMessage();



        if (chatMode == ChatMode.LOCAL)
            sendLocalMessage(nameAndMessage, clone);
        else if (chatMode == ChatMode.BATTALION_COMMS)
            sendBattalionCommsMessage(clone, e.getMessage());
        else if (chatMode == ChatMode.OUT_OF_CHARACTER)
            sendMessage(ChatColor.WHITE + "[OOC] " + nameAndMessage, MessageType.OOC);
        else if (chatMode == ChatMode.SERVER_COMMS)
            sendMessage(ChatColor.DARK_GREEN + "[Communications] " + nameAndMessage, MessageType.SERVER_COMMS);
        else if (chatMode == ChatMode.STAFF)
            sendStaffMessage(ChatColor.LIGHT_PURPLE + "[Staff] " + nameAndMessage);
        else if (chatMode == ChatMode.OFFICER)
            sendOfficerMessage(ChatColor.DARK_PURPLE + "[Officer] " + nameAndMessage);
        else if (chatMode == ChatMode.BROADCAST)
            sendMessage(ChatColor.RED + "[Broadcast] " + nameAndMessage, MessageType.BROADCAST);
    }

    public void sendLocalMessage(String nameAndMessage, Clone clone) {
        GroupMessage.sendGroupMessage(ChatColor.YELLOW + "[Local] " + nameAndMessage, getLocalPlayers(clone.getPlayer().getLocation()), MessageType.LOCAL);
    }

    public void sendBattalionCommsMessage(Clone clone, String message) {
        clone.getBattalion().sendBattalionCommsMessage(clone, message);
    }

    public void sendStaffMessage(String prefixNameAndMessage) {
        Bukkit.broadcast(prefixNameAndMessage, "CWB.staffChat");
    }

    public void sendOfficerMessage(String prefixNameAndMessage) {
        Bukkit.broadcast(prefixNameAndMessage, "CWB.officerChat");
    }

    public void sendMessage(String prefixNameAndMessage, MessageType messageType) {
        GroupMessage.sendGroupMessage(prefixNameAndMessage, new ArrayList<>(Bukkit.getOnlinePlayers()), messageType);
    }

    private List<Player> getLocalPlayers(Location loc) {
        return new ArrayList<>(loc.getNearbyEntitiesByType(Player.class, 15));
    }
}
