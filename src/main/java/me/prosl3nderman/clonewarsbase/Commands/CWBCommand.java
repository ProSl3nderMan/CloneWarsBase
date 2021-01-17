package me.prosl3nderman.clonewarsbase.Commands;

import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.Battalion;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Configs.ConfigHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.customskins.CustomSkins;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

@Singleton
public class CWBCommand implements CommandExecutor {

    private ConfigHandler configHandler;
    private BattalionHandler battalionHandler;
    private CloneWarsBase plugin;
    private MySQLDatabase mySQLDatabase;
    private CloneHandler cloneHandler;

    @Inject
    public CWBCommand(ConfigHandler configHandler, BattalionHandler battalionHandler, CloneWarsBase plugin, MySQLDatabase mySQLDatabase, CloneHandler cloneHandler) {
        this.configHandler = configHandler;
        this.battalionHandler = battalionHandler;
        this.plugin = plugin;
        this.mySQLDatabase = mySQLDatabase;
        this.cloneHandler = cloneHandler;
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

        if (subcommand.equalsIgnoreCase("batt")) {
            battalionCommands(playerSender, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("rankup")) {
            rankupCommand(playerSender, args);
            return true;
        }

        if (!playerSender.hasPermission("CWB.Testing")) {
            playerSender.sendMessage(ChatColor.RED + "Error, the subcommand " + wh + subcommand + rd + " does not exist.");
            return true;
        }

        if (subcommand.equalsIgnoreCase("createConfig")) {
            createConfig(playerSender, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("reloadConfigs")) {
            reloadConfigs(playerSender);
            return true;
        }

        if (subcommand.equalsIgnoreCase("reloadSkins")) {
            battalionHandler.goThroughBattalionDirectoryAndLoadBattalions(new File(plugin.getDataFolder() + File.separator + "battalions"), true, true);
            playerSender.sendMessage(ChatColor.GREEN + "All skin files are currently being reloaded. This can take a while, and you won't receive a message in game saying it's done.");
            playerSender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "BTW: Players will have to re-connect to see their own skins updated.");
            return true;
        }

        if (subcommand.equalsIgnoreCase("getServerTimezone")) {
            Bukkit.getLogger().log(Level.INFO, TimeZone.getDefault().getDisplayName());
            return true;
        }

        if (subcommand.equalsIgnoreCase("testSkin")) {
            CustomSkins customSkins = JavaPlugin.getPlugin(CustomSkins.class);
            String fs = File.separator;
            customSkins.createSkin("cr", new File(plugin.getDataFolder() + fs + "battalions" + fs + "cr" + fs, "cr.png"), "private", "steve", true);
            customSkins.applySkin("cr", playerSender.getName());
            playerSender.sendMessage(ChatColor.GREEN + "Skin set to cr! - ProSl3nderMan");
            return true;
        }

        playerSender.sendMessage(ChatColor.RED + "Error, the subcommand " + wh + subcommand + rd + " does not exist.");
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
        player.sendMessage(commandStarter + "batt create <battalion name>" + commandAndDescriptionSpacer + "Used to create a battalion. Auto generates the config file with default values.");
        player.sendMessage(commandStarter + "batt delete <battalion name>" + commandAndDescriptionSpacer + "Used to delete a battalion.");
        player.sendMessage(commandStarter + "batt setspawn <battalion name>" + commandAndDescriptionSpacer + "Used to set the spawnpoint of a battalion.");
        player.sendMessage(commandStarter + "batt forceJoin <battalion name> [rank]" + commandAndDescriptionSpacer + "Used to force join a battalion.");
        player.sendMessage(commandStarter + "batt list" + commandAndDescriptionSpacer + "Used to list all created battalions.");
        player.sendMessage(commandStarter + "rankup <cr name>" + commandAndDescriptionSpacer + "Used to promote a cr to a ct.");
        player.sendMessage(lineSpacer + helpTitleColors + " Administrating Commands " + lineSpacer);
        if (player.hasPermission(testingPermission)) {
            player.sendMessage(lineSpacer + helpTitleColors + " Testing Commands " + lineSpacer);
            player.sendMessage(commandStarter + "createConfig <config name> [folder]" + commandAndDescriptionSpacer + "Used to create a config with the name specified.");
            player.sendMessage(commandStarter + "reloadConfigs" + commandAndDescriptionSpacer + "Used to reload all configurations.");
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
                    String battalionNameStringBuilderr = ChatColor.GREEN + battalionNames.get(0);
                    for (int i = 1; i < battalionNames.size(); i++)
                        battalionNameStringBuilderr+= ChatColor.GOLD + ", " + ChatColor.GREEN + battalionNames.get(i);
                    String battalionNameStringBuilder = battalionNameStringBuilderr;
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            player.sendMessage(ChatColor.DARK_GREEN + "Battalions: " + battalionNameStringBuilder + ChatColor.DARK_GREEN + ".");
                        }
                    });
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
            if (battalionName.equalsIgnoreCase("cr") || battalionName.equalsIgnoreCase("ct")) {
                player.sendMessage(ChatColor.RED + "You cannot delete the default battalions " + wh + "cr" + rd + " and " + wh + "ct" + rd + "!");
                return;
            }
            battalionHandler.deleteBattalion(player, battalionName);
            return;
        }

        Battalion battalion = battalionHandler.getBattalion(battalionName);
        if (battalion == null) {
            player.sendMessage(rd + "Error! The battalion " + wh + battalionName + rd + " does not exist! Do " + wh + "/cwb batt list" + rd + " for a list of battalions!");
            return;
        }

        if (batSubcommand.equalsIgnoreCase("setspawn")) {
            battalion.setSpawnPoint(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "The battalion " + wh + battalionName + ChatColor.GREEN + " spawnpoint has been set to your location!");
            return;
        }

        if (batSubcommand.equalsIgnoreCase("forceJoin")) {
            String rank = args.length > 3 ? args[3] : "pvt";
            Clone clone = cloneHandler.getClone(player.getUniqueId());
            clone.acceptedInvitationToBattalion(battalion);
            cloneHandler.cloneForceRankup(clone.getName(), rank);
            return;
        }
    }

    private void rankupCommand(Player player, String[] args) {
        if (args.length < 2) {
            sendNotEnoughArgumentsMessage(player);
            return;
        }
        Player targetCR = Bukkit.getPlayer(args[1]);
        if (targetCR == null || !targetCR.isOnline()) {
            player.sendMessage(ChatColor.RED + "Error! The player " + wh + args[1] + rd + " either doesn't exist or is offline.");
            return;
        }
        if (!cloneHandler.getClone(targetCR.getUniqueId()).getRank().equalsIgnoreCase("cr")) {
            player.sendMessage(ChatColor.RED + "Error! The player " + wh + targetCR.getName()+ rd + " is not a Clone Recruit!");
            return;
        }
        cloneHandler.cloneForceRankup(targetCR.getName(), "null");
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.GREEN + "The player " + ChatColor.WHITE + targetCR.getName() + ChatColor.GREEN + " has been ranked up to CR!");
            }
        }, 20L);
    }

    private void reloadConfigs(Player player) {
        configHandler.reloadAllConfigs();
        player.sendMessage(ChatColor.GREEN + "All configuration files have been reloaded!");
    }
}
