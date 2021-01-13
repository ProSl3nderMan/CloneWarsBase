package me.prosl3nderman.clonewarsbase.Util;

import me.prosl3nderman.clonewarsbase.Internal.Player.Player;

import java.util.List;

public class GroupMessage {

    public static void sendGroupMessage(String message, List<Player> group, MessageType messageType) {
        for (Player player : group)
            player.getPlayer().sendMessage(message);
    }
}
