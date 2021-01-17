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
import java.util.UUID;

@Singleton
public class ChatHandler implements Handler {

    private CloneWarsBase plugin;
    private CloneHandler cloneHandler;

    @Inject
    public ChatHandler(CloneWarsBase plugin, CloneHandler cloneHandler) {
        this.plugin = plugin;
        this.cloneHandler = cloneHandler;
    }

    private HashMap<ChatMode, String> chatModePrefixs = new HashMap<>();
    private HashMap<ChatMode, String> chatModePermissions = new HashMap<>();

    @Override
    public void enable() {
        for (ChatMode chatMode : ChatMode.values()) {
            String pathToChatMode = "chat." + chatMode.name().toLowerCase() + ".";
            chatModePrefixs.put(chatMode, ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString(pathToChatMode + "prefix")));
            chatModePermissions.put(chatMode, plugin.getConfig().getString(pathToChatMode + "permission"));
        }
    }

    @Override
    public void disable() {

    }

    public void handleChatMessage(UUID playerUUID, String message) {
        Clone clone = cloneHandler.getClone(playerUUID);
        ChatMode chatMode = clone.getChatMode();

        handleChatMessage(clone, message, chatMode);
    }

    public void handleChatMessage(Clone clone, String message, ChatMode chatMode) {
        String wholeMessage = chatModePrefixs.get(chatMode) + " " + clone.getBattalion().getColoredAbbreviatedName() + " " + clone.getColoredRank() + " " + clone.getName() + ChatColor.WHITE + ": " + message;

        if (chatMode == ChatMode.LOCAL)
            sendMessage(wholeMessage, getLocalPlayers(clone.getPlayer().getLocation()), chatMode);
        else if (chatMode == ChatMode.BATTALION_COMMS)
            sendMessage(wholeMessage, clone.getBattalion().getOnlineClonesInPlayerForm(), chatMode);
        else if (chatMode == ChatMode.STAFF || chatMode == ChatMode.OFFICER)
            sendPermissionMessage(wholeMessage, chatModePermissions.get(chatMode));
        else
            sendMessage(wholeMessage, new ArrayList<>(Bukkit.getOnlinePlayers()), chatMode);
    }

    public Boolean cloneDoesNotHavePermissionForChatMode(Clone clone, ChatMode chatMode) {
        if (!clone.hasPermission(chatModePermissions.get(chatMode))) {
            clone.sendMessage(ChatColor.RED + "You do not have permission for the chat " + ChatColor.WHITE + chatMode.name().toLowerCase() + ChatColor.RED + "!");
            return true;
        }
        return false;
    }

    public void sendPermissionMessage(String wholeMessage, String permission) {
        Bukkit.broadcast(wholeMessage, permission);
    }

    public void sendMessage(String wholeMessage, List<Player> players, ChatMode chatMode) {
        GroupMessage.sendGroupMessage(wholeMessage, players, chatMode);
    }

    private List<Player> getLocalPlayers(Location loc) {
        return new ArrayList<>(loc.getNearbyEntitiesByType(Player.class, 15));
    }
}
