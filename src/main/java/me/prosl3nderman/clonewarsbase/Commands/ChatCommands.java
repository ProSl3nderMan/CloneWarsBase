package me.prosl3nderman.clonewarsbase.Commands;

import me.prosl3nderman.clonewarsbase.Internal.Chat.ChatHandler;
import me.prosl3nderman.clonewarsbase.Internal.Chat.ChatMode;
import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.logging.Level;

@Singleton
public class ChatCommands implements CommandExecutor {

    private CloneHandler cloneHandler;
    private ChatHandler chatHandler;

    @Inject
    public ChatCommands(CloneHandler cloneHandler, ChatHandler chatHandler) {
        this.cloneHandler = cloneHandler;
        this.chatHandler = chatHandler;
    }

    private ChatColor rd = ChatColor.RED, wh = ChatColor.WHITE;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String playerUsedCommand, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to do " + playerUsedCommand + "!");
            return true;
        }

        Clone clone = cloneHandler.getClone(((Player) commandSender).getUniqueId());

        ChatMode chatMode = null;

        if (command.getLabel().equalsIgnoreCase("ooc"))
            chatMode = ChatMode.OUT_OF_CHARACTER;
        else if (command.getLabel().equalsIgnoreCase("staffChat"))
            chatMode = ChatMode.STAFF;
        else if (command.getLabel().equalsIgnoreCase("officerChat"))
            chatMode = ChatMode.OFFICER;
        else if (command.getLabel().equalsIgnoreCase("comms"))
            chatMode = ChatMode.SERVER_COMMS;
        else if (command.getLabel().equalsIgnoreCase("battComms"))
            chatMode = ChatMode.BATTALION_COMMS;
        else if (command.getLabel().equalsIgnoreCase("broadcast"))
            chatMode = ChatMode.BROADCAST;
        else if (command.getLabel().equalsIgnoreCase("local"))
            chatMode = ChatMode.LOCAL;

        chatCommand(clone, args, chatMode);
        return true;
    }

    private void chatCommand(Clone clone, String[] args, ChatMode chatMode) {
        if (chatHandler.cloneDoesNotHavePermissionForChatMode(clone, chatMode)) //checks if player has the permission for the chat mode specified, if not it will tell the player.
            return;

        if (args.length == 0) { //player is attempting to toggle chatmode on.
            clone.toggleChatMode(chatMode); //toggles chat mode on or off, and informs the player for us.
            return;
        } else { //payer is sending a message through the chatmode specified.
            String message = args[0];
            for (int i = 1; i < args.length; i++)
                message += " " + args[i];

            chatHandler.handleChatMessage(clone, message, chatMode);
        }
    }
}
