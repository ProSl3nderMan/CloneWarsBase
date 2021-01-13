package me.prosl3nderman.clonewarsbase.Internal.Battalions;

import me.prosl3nderman.clonewarsbase.Internal.Storage.ConfigHandler;
import me.prosl3nderman.clonewarsbase.Internal.Player.Player;
import me.prosl3nderman.clonewarsbase.Util.GroupMessage;
import me.prosl3nderman.clonewarsbase.Util.HexColor;
import me.prosl3nderman.clonewarsbase.Util.MessageType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Battalion {

    private ConfigHandler configHandler;

    @Inject
    public Battalion(ConfigHandler configHandler) {
        this.configHandler = configHandler;
    }

    private String battalionName;
    private String properBattalionName;
    private File cloneSkin;
    private ChatColor battalionColor;
    private List<Player> onlinePlayers = new ArrayList<>();
    private String abbreviatedBattalionName;

    public void loadVariables(String battalionName) {
        this.battalionName = battalionName;
        this.properBattalionName = getConfig().getString("properBattalionName");
        this.battalionColor = ChatColor.of("#" + getConfig().getString("battalionColor"));
        this.abbreviatedBattalionName = getConfig().getString("abbreviatedBattalionName");
    }

    private FileConfiguration getConfig() {
        return configHandler.getConfig(battalionName, "battalions" + File.separator + battalionName).getConfig();
    }

    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void battalionPlayerCameOnline(Player player) {
        onlinePlayers.add(player);
        GroupMessage.sendGroupMessage(battalionColor + "" + ChatColor.BOLD + player.getPlayerName()
                        + ChatColor.BLUE + "" + ChatColor.BOLD + " has joined.",
                onlinePlayers, MessageType.BATTALION_JOIN_LEAVE);
    }

    public void battalionPlayerWentOffline(Player player) {
        onlinePlayers.remove(player);
        GroupMessage.sendGroupMessage(battalionColor + "" + ChatColor.BOLD + player.getPlayerName()
                        + ChatColor.BLUE + "" + ChatColor.BOLD + " has left.",
                onlinePlayers, MessageType.BATTALION_JOIN_LEAVE);
    }

    public void sendBattalionChatMessage(Player player, String message) {
        GroupMessage.sendGroupMessage(battalionColor + player.getRankName() + player.getPlayerName() + ChatColor.GOLD + ": "
                        + ChatColor.GREEN + message,
                onlinePlayers, MessageType.BATTALION_CHAT);
    }

    public String getBattalionName() {
        return battalionName;
    }

    public String getAbbreviatedBattalionName() {
        return abbreviatedBattalionName;
    }

    public ChatColor getBattalionColor() {
        return battalionColor;
    }

}
