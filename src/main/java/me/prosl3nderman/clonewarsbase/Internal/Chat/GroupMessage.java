package me.prosl3nderman.clonewarsbase.Internal.Chat;

import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import org.bukkit.entity.Player;

import java.util.List;

public class GroupMessage {

    public static void sendCloneGroupMessage(String message, List<Clone> group, ChatMode chatType) {
        for (Clone clone : group)
            clone.getPlayer().sendMessage(message);
    }

    public static void sendGroupMessage(String message, List<Player> group, ChatMode chatMode) {
        for (Player player : group)
            player.sendMessage(message);
    }
}
