package me.prosl3nderman.clonewarsbase.Internal.Battalions;

import me.prosl3nderman.clonewarsbase.Internal.Storage.Configs.ConfigHandler;
import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Util.GroupMessage;
import me.prosl3nderman.clonewarsbase.Util.MessageType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Battalion {

    private ConfigHandler configHandler;

    @Inject
    public Battalion(ConfigHandler configHandler) {
        this.configHandler = configHandler;
    }

    private String name;
    private String properName;
    private File cloneSkin;
    private ChatColor color;
    private List<Clone> onlineClones = new ArrayList<>();
    private String abbreviatedName;

    public void loadVariables(String battalionName) {
        this.name = battalionName;
        this.properName = getConfig().getString("properBattalionName");
        this.color = ChatColor.of("#" + getConfig().getString("battalionColor"));
        this.abbreviatedName = getConfig().getString("abbreviatedBattalionName");
        if (onlineClones.size() != 0)
            onlineClones.forEach(clone -> clone.updateTags());
    }

    private FileConfiguration getConfig() {
        return configHandler.getConfig(name, "battalions" + File.separator + name).getConfig();
    }

    public List<Clone> getOnlineClones() {
        return onlineClones;
    }

    public void battalionsCloneCameOnline(Clone clone) {
        onlineClones.add(clone);
        GroupMessage.sendGroupMessage(color + "" + ChatColor.BOLD + clone.getRank() + " " + clone.getName()
                        + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + " has joined.",
                onlineClones, MessageType.BATTALION_JOIN_LEAVE);
    }

    public void battalionsCloneWentOffline(Clone clone) {
        onlineClones.remove(clone);
        GroupMessage.sendGroupMessage(color + "" + ChatColor.BOLD + clone.getRank() + " " + clone.getName()
                        + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + " has left.",
                onlineClones, MessageType.BATTALION_JOIN_LEAVE);
    }

    public void sendBattalionCommsMessage(Clone clone, String message) {
        GroupMessage.sendGroupMessage(clone.getColoredRank() + " " + clone.getName() + ChatColor.GOLD + ": "
                        + ChatColor.GREEN + message,
                onlineClones, MessageType.BATTALION_CHAT);
    }

    public void cloneRankedUp(Clone clone, String rank) {
        if (!onlineClones.contains(clone))
            onlineClones.add(clone);
        ChatColor bc = color; ChatColor bo = ChatColor.BOLD; ChatColor wh = ChatColor.WHITE;
        GroupMessage.sendGroupMessage(bc + "" + bo + "Congrats to " + wh + bo + clone.getName() + bc + bo + " for rank " + wh + bo + rank.toUpperCase() + bc + bo + "!",
                onlineClones, MessageType.BATTALION_CHAT);
    }

    public void removeCloneFromBattalion(Clone clone, String removalMessage) {
        ChatColor bo = ChatColor.BOLD;
        GroupMessage.sendGroupMessage(getColor() + "" + bo + clone.getRank() + " " + clone.getName() + " " + ChatColor.WHITE + ChatColor.BOLD + removalMessage, onlineClones, MessageType.BATTALION_CHAT);
        onlineClones.remove(clone);
    }

    public void removeCloneFromBattalion(String clone, String removalMessage) {
        GroupMessage.sendGroupMessage(getColor() + clone + " " + ChatColor.WHITE + ChatColor.BOLD + removalMessage, onlineClones, MessageType.BATTALION_CHAT);
    }

    public void silentCloneJoin(Clone clone) {
        onlineClones.add(clone);
    }

    public String getName() {
        return name;
    }

    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    public ChatColor getColor() {
        return color;
    }

}
