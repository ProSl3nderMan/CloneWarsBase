package me.prosl3nderman.clonewarsbase.Internal.Battalions;

import me.prosl3nderman.clonewarsbase.Internal.Chat.ChatMode;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Configs.Config;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Configs.ConfigHandler;
import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Internal.Wrappers.ProLocation;
import me.prosl3nderman.clonewarsbase.Internal.Chat.GroupMessage;
import me.prosl3nderman.clonewarsbase.Util.LocationStringConverter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
    private ProLocation spawnLocation;

    public void loadVariables(String battalionName) {
        this.name = battalionName;
        this.properName = getConfig().getString("properBattalionName");
        this.color = ChatColor.of("#" + getConfig().getString("battalionColor"));
        this.abbreviatedName = getConfig().getString("abbreviatedBattalionName");
        this.spawnLocation = new ProLocation(LocationStringConverter.getLocationFromString(getConfig().getString("spawnpoint"), true));
        if (onlineClones.size() != 0)
            onlineClones.forEach(clone -> clone.updateTags());
    }

    private FileConfiguration getConfig() {
        return configHandler.getConfig(name, "battalions" + File.separator + name).getConfig();
    }
    private Config getConfigFile() {
        return configHandler.getConfig(name, "battalions" + File.separator + name);
    }

    public List<Clone> getOnlineClones() {
        return onlineClones;
    }

    public List<Player> getOnlineClonesInPlayerForm() {
        List<Player> onlineClonesInPlayerForm = new ArrayList<>();
        onlineClones.forEach(clone -> onlineClonesInPlayerForm.add(clone.getPlayer()));
        return onlineClonesInPlayerForm;
    }

    public void battalionsCloneCameOnline(Clone clone) {
        onlineClones.add(clone);
        GroupMessage.sendCloneGroupMessage(color + "" + ChatColor.BOLD + clone.getRank() + " " + clone.getName()
                        + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + " has joined.", onlineClones, ChatMode.BATTALION_COMMS);
    }

    public void battalionsCloneWentOffline(Clone clone) {
        onlineClones.remove(clone);
        GroupMessage.sendCloneGroupMessage(color + "" + ChatColor.BOLD + clone.getRank() + " " + clone.getName()
                        + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + " has left.", onlineClones, ChatMode.BATTALION_COMMS);
    }

    public void cloneRankedUp(Clone clone, String rank) {
        if (!onlineClones.contains(clone))
            onlineClones.add(clone);
        ChatColor bc = color; ChatColor bo = ChatColor.BOLD; ChatColor wh = ChatColor.WHITE;
        GroupMessage.sendCloneGroupMessage(bc + "" + bo + "Congrats to " + wh + bo + clone.getName() + bc + bo + " for rank " + wh + bo + rank.toUpperCase() + bc + bo + "!"
                , onlineClones, ChatMode.BATTALION_COMMS);
    }

    public void removeCloneFromBattalion(Clone clone, String removalMessage) {
        ChatColor bo = ChatColor.BOLD;
        GroupMessage.sendCloneGroupMessage(getColor() + "" + bo + clone.getRank() + " " + clone.getName() + " " + ChatColor.WHITE + ChatColor.BOLD + removalMessage
                , onlineClones, ChatMode.BATTALION_COMMS);
        onlineClones.remove(clone);
    }

    public void removeCloneFromBattalion(String clone, String removalMessage) {
        GroupMessage.sendCloneGroupMessage(getColor() + clone + " " + ChatColor.WHITE + ChatColor.BOLD + removalMessage, onlineClones, ChatMode.BATTALION_COMMS);
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

    public Location getSpawnPoint() {
        return spawnLocation.getLocation();
    }
    public void setSpawnPoint(Location location) {
        getConfig().set("spawnpoint", LocationStringConverter.getStringFromLocation(location, true));
        getConfigFile().srConfig();
        spawnLocation = new ProLocation(location);
    }

    public String getColoredAbbreviatedName() {
        return color + abbreviatedName.toUpperCase();
    }

}
