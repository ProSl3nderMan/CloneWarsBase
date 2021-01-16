package me.prosl3nderman.clonewarsbase.Util;

import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;

import java.util.List;

public class GroupMessage {

    public static void sendGroupMessage(String message, List<Clone> group, MessageType messageType) {
        for (Clone clone : group)
            clone.getPlayer().sendMessage(message);
    }
}
