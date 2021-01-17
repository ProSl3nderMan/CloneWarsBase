package me.prosl3nderman.clonewarsbase.Internal.Clone;

import me.prosl3nderman.clonewarsbase.Internal.APIs.SkinAPI;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.Battalion;
import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.APIs.LuckPermsAPI;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import me.prosl3nderman.clonewarsbase.Internal.APIs.TabAPI;
import me.prosl3nderman.clonewarsbase.Internal.Chat.ChatMode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Clone {

    private CloneWarsBase plugin;
    private LuckPermsAPI luckPermsAPI;
    private MySQLDatabase mySQLDatabase;
    private TabAPI tabAPI;
    private BattalionHandler battalionHandler;
    private SkinAPI skinAPI;

    @Inject
    public Clone(CloneWarsBase plugin, LuckPermsAPI luckPermsAPI, MySQLDatabase mySQLDatabase, TabAPI tabAPI, BattalionHandler battalionHandler, SkinAPI skinAPI) {
        this.plugin = plugin;
        this.luckPermsAPI = luckPermsAPI;
        this.mySQLDatabase = mySQLDatabase;
        this.tabAPI = tabAPI;
        this.battalionHandler = battalionHandler;
        this.skinAPI = skinAPI;
    }

    private UUID UUID;
    private String name;
    private Integer cloneID;
    private String rank;
    private Battalion battalion;
    private Player player;

    private List<String> battalionInvites;
    private ChatMode chatMode;

    public void loadPlayerVariables(Player player) {
        UUID = player.getUniqueId();
        name = player.getName();
        this.player = player;
        battalionInvites = new ArrayList<>();
        chatMode = ChatMode.LOCAL;
        Clone clone = this;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                cloneID = mySQLDatabase.updatePlayerInformationAndGetPlayerID(clone);
                rank = luckPermsAPI.getPlayerRank(UUID);
                if (!rank.equalsIgnoreCase("cr") && !rank.equalsIgnoreCase("ct"))
                    battalion = mySQLDatabase.getPlayersBattalion(name);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (rank.equalsIgnoreCase("cr") || rank.equalsIgnoreCase("ct"))
                            battalion = battalionHandler.getBattalion(rank);
                        updateTags();
                        battalion.battalionsCloneCameOnline(clone);
                    }
                });
            }
        });
    }

    public void wentOffline() {
        battalion.battalionsCloneWentOffline(this);
        battalionInvites.clear();
        player = null;
    }

    public UUID getUUID() {
        return UUID;
    }
    public String getName() { return name; }

    public Player getPlayer() {
        return player;
    }

    public String getRank() {
        return rank.toUpperCase();
    }

    public String getColoredRank() {
        return getBattalion().getColor() + getRank();
    }

    public String getRankTitle() {
        return battalion.getName().equalsIgnoreCase("cr") || battalion.getName().equalsIgnoreCase("ct")
                ?
                battalion.getColor() + getRank().toUpperCase() + " " + cloneID + " "
                :
                battalion.getColor() + battalion.getAbbreviatedName() + " " + getRank().toUpperCase() + " ";
    }
    
    public Battalion getBattalion() {
        return battalion;
    }

    public void updateRank(String rank) {
        this.rank = rank;
        updateTags();
        skinAPI.applyCloneSkin(getPlayer(), battalion, rank);
    }

    public void updateTags() {
        tabAPI.setPlayersBelowNameTitle(getRankTitle(), getPlayer());
        tabAPI.setPlayersTabTitle(getRankTitle() + "| " + name, getPlayer());
        tabAPI.setPlayersNameColor(battalion.getColor(), getPlayer());
    }

    public void sendMessage(String message) {
        getPlayer().sendMessage(message);
    }

    public Boolean hasPermission(String permission) {
        return getPlayer().hasPermission(permission);
    }

    public void invitedToBattalion(Battalion battalion) {
        String battalionName = battalion.getName();
        battalionInvites.add(battalionName);

        sendMessage(ChatColor.GREEN + "You have been invited to the battalion " + battalion.getColor() + battalionName.toUpperCase() + ChatColor.GREEN + "!");

        String hoverMessage = ChatColor.WHITE + "" + ChatColor.BOLD + "Click Here to join the battalion!";
        String messagee = ChatColor.GREEN + "Do " + ChatColor.WHITE + "/batt join " + battalionName + ChatColor.GREEN + " or CLICK HERE to join.";

        TextComponent message = new TextComponent(messagee);
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/batt join " + battalionName));

        getPlayer().spigot().sendMessage(new ComponentBuilder(message).create());
    }

    public void acceptedInvitationToBattalion(Battalion battalion) {
        battalionInvites.remove(battalion.getName());
        this.battalion = battalion;
        Clone clone = this;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                mySQLDatabase.addPlayerToBattalion(clone, battalion.getName());
            }
        });
    }

    public List<String> getBattalionInvites() {
        return battalionInvites;
    }

    public void leftBattalion() {
        battalion = battalionHandler.getBattalion("ct");
        battalion.silentCloneJoin(this);
        updateRank("ct");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                luckPermsAPI.setPlayerRank(getUUID(), "ct");
                mySQLDatabase.removePlayerFromBattalion(name);
            }
        });
    }

    public void toggleBattalionComms() {
        if (chatMode == ChatMode.BATTALION_COMMS)
            toggleBattalionComms("off");
        else
            toggleBattalionComms("on");
    }

    public void toggleBattalionComms(String toggleStatus) {
        if (toggleStatus.equalsIgnoreCase("on")) {
            chatMode = ChatMode.BATTALION_COMMS;
            sendMessage(ChatColor.DARK_GREEN + "Battalion communications has been toggled " + ChatColor.GREEN + "on" + ChatColor.DARK_GREEN + "!");
        } else {
            chatMode = ChatMode.LOCAL;
            sendMessage(ChatColor.DARK_GREEN + "Battalion communications has been toggled " + ChatColor.RED + "off" + ChatColor.DARK_GREEN + "!");
        }
    }

    public ChatMode getChatMode() {
        return chatMode;
    }
}
