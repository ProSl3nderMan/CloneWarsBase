package me.prosl3nderman.clonewarsbase.Internal.APIs;

import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class TabAPI {

    public void setPlayersAboveNameTitle(String rankTitle, Player player) {
        TABAPI.getPlayer(player.getUniqueId()).setValueTemporarily(EnumProperty.ABOVENAME, rankTitle);
    }

    public void setPlayersTabTitle(String rankTitleAndName, Player player) {
        TABAPI.getPlayer(player.getUniqueId()).setValueTemporarily(EnumProperty.CUSTOMTABNAME, rankTitleAndName);
    }

    public void setPlayersNameColor(ChatColor battalionColor, Player player) {
        TABAPI.getPlayer(player.getUniqueId()).setValueTemporarily(EnumProperty.TAGPREFIX, battalionColor + "");
    }
}
