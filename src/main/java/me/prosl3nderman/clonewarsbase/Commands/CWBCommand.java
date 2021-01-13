package me.prosl3nderman.clonewarsbase.Commands;

import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.ConfigHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class CWBCommand implements CommandExecutor {

    private ConfigHandler configHandler;
    private BattalionHandler battalionHandler;
    private CloneWarsBase plugin;
    private MySQLDatabase mySQLDatabase;

    @Inject
    public CWBCommand(ConfigHandler configHandler, BattalionHandler battalionHandler, CloneWarsBase plugin, MySQLDatabase mySQLDatabase) {
        this.configHandler = configHandler;
        this.battalionHandler = battalionHandler;
        this.plugin = plugin;
        this.mySQLDatabase = mySQLDatabase;
    }

    private String testingPermission = "CWB.Testing";
    private ChatColor wh = ChatColor.WHITE;
    private ChatColor rd = ChatColor.RED;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Must be a player to do this command!");
            return true;
        }

        Player playerSender = (Player) sender;

        if (!playerSender.hasPermission("CWB.Admin")) {
            playerSender.sendMessage(ChatColor.RED + "You do not have permission to do this command!");
            return true;
        }

        String subcommand = args.length == 0 ? "help" : args[0];

        if (subcommand.equalsIgnoreCase("help")) {
            sendHelpMessage(playerSender);
            return true;
        }

        if (!playerSender.hasPermission("CWB.Testing")) {
            playerSender.sendMessage(ChatColor.RED + "You do not have testing permissions to do this command!");
            return true;
        }

        if (subcommand.equalsIgnoreCase("createConfig")) {
            createConfig(playerSender, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("batt")) {
            battalionCommands(playerSender, args);
            return true;
        }
        return true;
    }

    private void sendNotEnoughArgumentsMessage(Player player) {
        player.sendMessage(rd + "Not enough arguments! Do " + wh + "/CWB help" + rd + " for help.");
    }

    private void sendHelpMessage(Player player) {
        String lineSpacer = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "***************";
        String helpTitleColors = ChatColor.GRAY + "" + ChatColor.BOLD;
        String commandStarter = ChatColor.GRAY + "" + ChatColor.BOLD + "/CWB ";
        String commandAndDescriptionSpacer = ChatColor.GOLD + "" + ChatColor.BOLD + " || " + ChatColor.WHITE;

        player.sendMessage(lineSpacer + helpTitleColors + " Administrating Commands " + lineSpacer);
        player.sendMessage(commandStarter + "batt create <battalion name>" + commandAndDescriptionSpacer
                    + "Used to create a battalion. Auto generates the config file with default values.");
        player.sendMessage(commandStarter + "batt delete <battalion name>" + commandAndDescriptionSpacer
                    + "Used to delete a battalion.");
        player.sendMessage(commandStarter + "batt list" + commandAndDescriptionSpacer
                    + "Used to list all created battalions.");
        player.sendMessage(lineSpacer + helpTitleColors + " Administrating Commands " + lineSpacer);
        if (player.hasPermission(testingPermission)) {
            player.sendMessage(lineSpacer + helpTitleColors + " Testing Commands " + lineSpacer);
            player.sendMessage(commandStarter + "createConfig <config name> [folder]" + commandAndDescriptionSpacer
                    + "Used to create a config with the name specified.");
            player.sendMessage(lineSpacer + helpTitleColors + " Testing Commands " + lineSpacer);
        } else
            player.sendMessage(ChatColor.DARK_GRAY + "Testing Commands have been excluded from your view, since " +
                    "you do not have the permission '" + ChatColor.GRAY + testingPermission + ChatColor.DARK_GRAY
                    + "'!");
    }

    private void createConfig(Player player, String[] args) {
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "You must include the config name that you wish to create!");
            return;
        }
        String configName = args[1].contains(".yml") ? args[1].replaceAll(".yml", "") : args[1];
        if (args.length < 3)
            configHandler.loadConfig(configName);
        else
            configHandler.loadConfig(configName, args[2]);
        player.sendMessage(ChatColor.DARK_GREEN + "The config " + ChatColor.GREEN + configName + ChatColor.DARK_GREEN
                + " has been created!");
    }

    private void battalionCommands(Player player, String[] args) {
        if (args.length < 2) {
            sendNotEnoughArgumentsMessage(player);
            return;
        }
        String batSubcommand = args[1];

        if (batSubcommand.equalsIgnoreCase("list")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    List<String> battalionNames = mySQLDatabase.getAllBattalionNames();
                    String battalionNameStringBuilder = battalionNames.get(0);
                    for (int i = 1; i < battalionNames.size(); i++)
                        battalionNameStringBuilder+= ChatColor.GOLD + ", " + battalionNames.get(i);
                    player.sendMessage(ChatColor.GREEN + "Battalions: " + battalionNameStringBuilder + ChatColor.GREEN + ".");
                }
            });
            return;
        }

        if (args.length < 3) {
            sendNotEnoughArgumentsMessage(player);
            return;
        }
        String battalionName = args[2];
        if (batSubcommand.equalsIgnoreCase("create")) {
            battalionHandler.createBattalion(player, battalionName);
            return;
        }
        if (batSubcommand.equalsIgnoreCase("delete")) {
            battalionHandler.deleteBattalion(player, battalionName);
            return;
        }
    }
}
