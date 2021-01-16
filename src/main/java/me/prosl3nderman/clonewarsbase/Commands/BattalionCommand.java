package me.prosl3nderman.clonewarsbase.Commands;

import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.APIs.LuckPermsAPI;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.Battalion;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.Internal.Clone.Clone;
import me.prosl3nderman.clonewarsbase.Internal.Clone.CloneHandler;
import me.prosl3nderman.clonewarsbase.Internal.Menus.ConfirmActionMenu;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

@Singleton
public class BattalionCommand implements CommandExecutor {

    private CloneHandler cloneHandler;
    private BattalionHandler battalionHandler;
    private MySQLDatabase mySQLDatabase;
    private CloneWarsBase plugin;
    private ConfirmActionMenu confirmActionMenu;
    private LuckPermsAPI luckPermsAPI;

    @Inject
    public BattalionCommand(CloneHandler cloneHandler, BattalionHandler battalionHandler, MySQLDatabase mySQLDatabase, CloneWarsBase plugin, ConfirmActionMenu confirmActionMenu, LuckPermsAPI luckPermsAPI) {
        this.cloneHandler = cloneHandler;
        this.battalionHandler = battalionHandler;
        this.mySQLDatabase = mySQLDatabase;
        this.plugin = plugin;
        this.confirmActionMenu = confirmActionMenu;
        this.luckPermsAPI = luckPermsAPI;
    }

    private String officerPermission = "CWB.Battalion.OfficerCommands";
    private ChatColor wh = ChatColor.WHITE;
    private ChatColor rd = ChatColor.RED;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Clone) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to do this command!");
            return true;
        }
        Clone clone = cloneHandler.getClone(((Player)commandSender).getUniqueId());

        String subcommand = args.length == 0 ? "help" : args[0];

        if (subcommand.equalsIgnoreCase("help")) {
            sendHelpMessage(clone);
            return true;
        }

        if (subcommand.equalsIgnoreCase("list")) {
            listBattalionClones(clone, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("join")) {
            joinBattalion(clone, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("leave")) {
            leaveBattalion(clone);
            return true;
        }

        if (subcommand.equalsIgnoreCase("chat") || subcommand.equalsIgnoreCase("c") || subcommand.equalsIgnoreCase("comms")) {
            toggleBattalionComms(clone, args);
            return true;
        }

        if (!clone.hasPermission(officerPermission)) {
            clone.sendMessage(rd + "The command " + wh + subcommand + rd + " does not exist! Do " + wh + "/batt help" + rd + " for a list of commands.");
            return true;
        }

        if (subcommand.equalsIgnoreCase("invite")) {
            inviteClone(clone, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("kick")) {
            kickClone(clone, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("promote")) {
            promoteClone(clone, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("demote")) {
            demoteClone(clone, args);
            return true;
        }

        if (subcommand.equalsIgnoreCase("rankset")) {
            rankSetClone(clone, args);
            return true;
        }
        return true;
    }

    private void sendHelpMessage(Clone clone) {
        String lineSpacer = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "***************";
        String titleColors = ChatColor.AQUA + "" + ChatColor.BOLD;
        String commandStarter = ChatColor.AQUA + "" + ChatColor.BOLD + "/batt ";
        String commandAndDescriptionSpacer = ChatColor.GOLD + "" + ChatColor.BOLD + " || " + ChatColor.WHITE;

        clone.sendMessage(lineSpacer + titleColors + " Battalion Commands " + lineSpacer);
        clone.sendMessage(commandStarter + "list [battalion || clone]" + commandAndDescriptionSpacer + "Used to retrieve a list of all battalion members. If you give a clone name, it'll pull up their battalion's members.");
        clone.sendMessage(commandStarter + "join [battalion]" + commandAndDescriptionSpacer + "Used to join a battalion. If there are multiple invites, specify the battalion.");
        clone.sendMessage(commandStarter + "leave" + commandAndDescriptionSpacer + "Used to leave a battalion.");
        clone.sendMessage(commandStarter + "<chat || c || comms> [on|off]" + commandAndDescriptionSpacer + "Used to toggle on/off battalion communications.");
        //player.sendMessage(commandStarter + "" + commandAndDescriptionSpacer + "");
        if (clone.hasPermission(officerPermission)) {
            clone.sendMessage(titleColors + "Clone Status Commands " + lineSpacer);
            clone.sendMessage(commandStarter + "invite <clone>" + commandAndDescriptionSpacer + "Used to invite a clone to the battalion.");
            clone.sendMessage(commandStarter + "kick <clone>" + commandAndDescriptionSpacer + "Used to kick a clone from the battalion.");
            clone.sendMessage(commandStarter + "promote <clone>" + commandAndDescriptionSpacer + "Used to promote a clone in the battalion.");
            clone.sendMessage(commandStarter + "demote <clone>" + commandAndDescriptionSpacer + "Used to demote a clone in the battalion.");
            clone.sendMessage(commandStarter + "rankset <rank> <clone>" + commandAndDescriptionSpacer + "Used to set a clone's rank in the battalion.");
        }
        clone.sendMessage(lineSpacer + titleColors + " Battalion Commands " + lineSpacer);
    }

    private void listBattalionClones(Clone clone, String[] args) {
        Battalion battalion = clone.getBattalion();
        if (args.length > 1) {
            String battalionOrClone = args[1];
            battalion = battalionHandler.getBattalion(battalionOrClone);
            if (battalion == null) {
                Battalion battalionn = battalion;
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Battalion newBattalion = battalionn;
                        if (battalionn == null)
                            newBattalion = mySQLDatabase.getPlayersBattalion(battalionOrClone);
                        if (newBattalion == null) {
                            clone.sendMessage(rd + "Error! The given option " + wh + battalionOrClone + rd + " is not a battalion or a clone!");
                            clone.sendMessage(rd + "If you want your own battalion's clone list, just do " + wh + "/batt list" + rd + ".");
                            return;
                        }
                        Battalion newBattalionn = newBattalion;
                        Bukkit.getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                sendBattalionPlayerList(clone, newBattalionn);
                            }
                        });
                    }
                });
                return;
            }
        }
        sendBattalionPlayerList(clone, battalion);
    }

    private void sendBattalionPlayerList(Clone clone, Battalion battalion) {
        String battalionName = battalion.getName();
        ChatColor battalionColor = battalion.getColor();
        List<String> battalionOnlineClones = new ArrayList<>();
        List<String> battalionOnlineCloneNamesWithColoredRanks = new ArrayList<>();
        battalion.getOnlineClones().forEach(onlineClone -> {
            battalionOnlineClones.add(onlineClone.getName());
            battalionOnlineCloneNamesWithColoredRanks.add(onlineClone.getColoredRank() + " " + onlineClone.getName());
        });

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<String> battalionCloneIGNs = mySQLDatabase.getAllBattalionCloneIGNs(battalionName);
                String allClonesString = battalionOnlineCloneNamesWithColoredRanks.size() != 0 ? battalionOnlineCloneNamesWithColoredRanks.get(0)
                        : battalionCloneIGNs.size() != 0 ? battalionCloneIGNs.get(0) : "";
                for (int i = 1; i < battalionOnlineCloneNamesWithColoredRanks.size(); i++)
                    allClonesString += wh + ", " + battalionOnlineCloneNamesWithColoredRanks.get(i);
                for (int i = 0; i < battalionCloneIGNs.size(); i++)
                    allClonesString += battalionOnlineClones.contains(battalionCloneIGNs.get(i)) ? "" : wh + ", " + ChatColor.GRAY + battalionCloneIGNs.get(i);
                final String allClonesStringFinal = allClonesString;
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        clone.sendMessage(wh + "All clones of battalion " + battalionColor + battalionName + wh + ": " + allClonesStringFinal);
                    }
                });
            }
        });
    }

    private void joinBattalion(Clone clone, String[] args) {
        List<String> battalionInvites = clone.getBattalionInvites();
        if (battalionInvites.size() == 0) {
            clone.sendMessage(rd + "Error! You must be invited to a battalion to join one!");
            return;
        }
        Battalion attemptedBattalion;
        if (battalionInvites.size() == 1)
            attemptedBattalion = battalionHandler.getBattalion(battalionInvites.get(0));
        else if (args.length == 1) {
            clone.sendMessage(rd + "Error! You've been invited to more than one battalion, please pick one to join and do " + wh + "/batt join <battalion name>" + rd + "!");
            clone.sendMessage(rd + "Battalions you've been invited to: " + wh + battalionInvites.toString());
            return;
        } else if (!battalionInvites.contains(args[1])) {
            clone.sendMessage(rd + "Error! You have not been invited to the battalion " + wh + args[1] + rd + "!");
            clone.sendMessage(rd + "Battalions you've been invited to: " + wh + battalionInvites.toString());
            return;
        } else
            attemptedBattalion = battalionHandler.getBattalion(args[1]);

        if (!clone.getRank().equalsIgnoreCase("ct")) {
            clone.sendMessage(rd + "In order to join a battalion, you must leave your battalion first! The command to leave your battalion is " + wh + "/batt leave");
            return;
        }

        clone.acceptedInvitationToBattalion(attemptedBattalion);
        cloneHandler.cloneRankup(clone.getName(), "null");
    }

    private void leaveBattalion(Clone clone) {
        if (clone.getBattalion().getName().equalsIgnoreCase("cr") || clone.getBattalion().getName().equalsIgnoreCase("ct")) {
            clone.sendMessage(rd + "Error! In order to leave a battalion, you must join one first!");
            return;
        }

        confirmActionMenu.openMenu(clone, "Leaving", clone.getName());
    }

    private void toggleBattalionComms(Clone clone, String[] args) {
        if (args.length > 1) {
            String toggleStatus = args[1];
            if (toggleStatus.equalsIgnoreCase("on") || toggleStatus.equalsIgnoreCase("off")) {
                clone.toggleBattalionComms(toggleStatus);
                return;
            }
            clone.sendMessage(rd + "Error! Toggle status must be " + wh + "on" + rd + " or " + wh + "off" + rd + "! You tried giving status: " + wh + toggleStatus + rd + "!");
            clone.sendMessage(ChatColor.DARK_RED + "Continuing to default toggle command...");
        }
        clone.toggleBattalionComms();
    }

    private Clone checkIfArgumentIsClone(Clone clone, String targetPlayerString) {
        Player targetPlayerClone = Bukkit.getPlayer(targetPlayerString);
        if (targetPlayerClone == null || !targetPlayerClone.isOnline()) {
            clone.sendMessage(rd + "Error! The clone " + wh + targetPlayerString + rd + " doesn't exist or is offline!");
            return null;
        }
        return cloneHandler.getClone(targetPlayerClone.getUniqueId());
    }

    private void inviteClone(Clone clone, String[] args) {
        if (args.length < 2) {
            clone.sendMessage(rd + "Error! Not enough arguments. Correct usage " + wh + "/batt invite <clone>" + rd + ".");
            return;
        }
        Clone targetClone = checkIfArgumentIsClone(clone, args[1]);
        if (targetClone == null)
            return;
        if (targetClone.getBattalion().getName().equalsIgnoreCase(clone.getBattalion().getName())) {
            clone.sendMessage(rd + "Error! The clone " + wh + targetClone.getName() + rd + " is already in your battalion!");
            return;
        }

        targetClone.invitedToBattalion(clone.getBattalion());
        clone.sendMessage(ChatColor.GREEN + "The clone " + wh + args[1] + ChatColor.GREEN + " has been invited to your battalion!");
    }

    private void kickClone(Clone clone, String[] args) {
        if (args.length < 2) {
            clone.sendMessage(rd + "Error! Not enough arguments. Correct usage " + wh + "/batt kick <clone>" + rd + ".");
            return;
        }

        String targetCloneName = args[1];
        String battalionName = clone.getBattalion().getName();
        UUID senderCloneUUID = clone.getUUID();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<UUID> allBattalionPlayerUUIDs = mySQLDatabase.getAllBattalionCloneUUIDs(battalionName);
                UUID targetCloneUUID = mySQLDatabase.getPlayerUUIDFromIGN(targetCloneName);
                boolean targetCloneRankIsLesserThanSenderCloneRank = targetCloneUUID == null ? false : luckPermsAPI.firstPersonsRankIsLesserThanSecondPersonsRank(targetCloneUUID, senderCloneUUID);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (targetCloneUUID == null) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " does not exist!");
                            return;
                        }
                        if (!allBattalionPlayerUUIDs.contains(targetCloneUUID)) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " is not in your battalion!");
                            return;
                        }
                        if (!targetCloneRankIsLesserThanSenderCloneRank) {
                            clone.sendMessage(rd + "Error! You can't kick a clone with the same or greater rank!");
                            return;
                        }
                        confirmActionMenu.openMenu(clone, "Kicking", targetCloneName);
                    }
                });
            }
        });
    }

    private void promoteClone(Clone clone, String[] args) {
        if (args.length < 2) {
            clone.sendMessage(rd + "Error! Not enough arguments. Correct usage " + wh + "/batt promote <clone>" + rd + ".");
            return;
        }

        String targetCloneName = args[1];
        if (clone.getName().equalsIgnoreCase(targetCloneName)) {
            clone.sendMessage(rd + "Error! You cannot promote yourself!");
            return;
        }

        String battalionName = clone.getBattalion().getName();
        UUID senderCloneUUID = clone.getUUID();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<UUID> allBattalionPlayerUUIDs = mySQLDatabase.getAllBattalionCloneUUIDs(battalionName);
                UUID targetCloneUUID = mySQLDatabase.getPlayerUUIDFromIGN(targetCloneName);
                boolean targetCloneRankIsLesserThanSenderCloneRank = targetCloneUUID == null ? false : luckPermsAPI.firstPersonsRankIsLesserThanSecondPersonsRank(targetCloneUUID, senderCloneUUID);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (targetCloneUUID == null) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " does not exist!");
                            return;
                        }
                        if (!allBattalionPlayerUUIDs.contains(targetCloneUUID)) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " is not in your battalion!");
                            return;
                        }
                        if (!targetCloneRankIsLesserThanSenderCloneRank) {
                            clone.sendMessage(rd + "Error! You can't promote a clone with the same or greater rank!");
                            return;
                        }
                        cloneHandler.cloneRankup(targetCloneName, "null");
                    }
                });
            }
        });
    }

    private void demoteClone(Clone clone, String[] args) {
        if (args.length < 2) {
            clone.sendMessage(rd + "Error! Not enough arguments. Correct usage " + wh + "/batt demote <clone>" + rd + ".");
            return;
        }

        String targetCloneName = args[1];
        if (clone.getName().equalsIgnoreCase(targetCloneName)) {
            clone.sendMessage(rd + "Error! You cannot demote yourself!");
            return;
        }

        String battalionName = clone.getBattalion().getName();
        UUID senderCloneUUID = clone.getUUID();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<UUID> allBattalionPlayerUUIDs = mySQLDatabase.getAllBattalionCloneUUIDs(battalionName);
                UUID targetCloneUUID = mySQLDatabase.getPlayerUUIDFromIGN(targetCloneName);
                boolean targetCloneRankIsLesserThanSenderCloneRank = luckPermsAPI.firstPersonsRankIsLesserThanSecondPersonsRank(targetCloneUUID, senderCloneUUID);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (targetCloneUUID == null) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " does not exist!");
                            return;
                        }
                        if (!allBattalionPlayerUUIDs.contains(targetCloneUUID)) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " is not in your battalion!");
                            return;
                        }
                        if (!targetCloneRankIsLesserThanSenderCloneRank) {
                            clone.sendMessage(rd + "Error! You can't demote a clone with the same or greater rank!");
                            return;
                        }
                        cloneHandler.cloneDerank(targetCloneName, "null");
                        clone.sendMessage(ChatColor.GREEN + "The clone " + wh + clone.getName() + ChatColor.GREEN + " has been demoted.");
                    }
                });
            }
        });
    }

    private void rankSetClone(Clone clone, String[] args) {
        if (args.length < 3) {
            clone.sendMessage(rd + "Error! Not enough arguments. Correct usage " + wh + "/batt rankset <rank> <clone>" + rd + ".");
            return;
        }

        String targetCloneName = args[2];
        boolean promotingDemotingSelf = clone.getName().equalsIgnoreCase(targetCloneName) ? true : false;

        String rank = args[1];
        String battalionName = clone.getBattalion().getName();
        UUID senderCloneUUID = clone.getUUID();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<UUID> allBattalionPlayerUUIDs = mySQLDatabase.getAllBattalionCloneUUIDs(battalionName);
                UUID targetCloneUUID = mySQLDatabase.getPlayerUUIDFromIGN(targetCloneName);
                boolean targetCloneRankIsLesserThanSenderCloneRank =  targetCloneUUID == null ? false : promotingDemotingSelf ? true : luckPermsAPI.firstPersonsRankIsLesserThanSecondPersonsRank(targetCloneUUID, senderCloneUUID);
                boolean rankIsLegit = rank.equalsIgnoreCase("null") ? true : luckPermsAPI.isRankLegit(rank);
                boolean givenRankIsGreaterThanGiversRank = !rankIsLegit ? true : luckPermsAPI.givenRankIsGreaterThanGiversRank(rank, senderCloneUUID);
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (targetCloneUUID == null) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " does not exist!");
                            return;
                        }
                        if (!allBattalionPlayerUUIDs.contains(targetCloneUUID)) {
                            clone.sendMessage(rd + "Error! The clone " + wh + targetCloneName + rd + " is not in your battalion!");
                            return;
                        }
                        if (!targetCloneRankIsLesserThanSenderCloneRank) {
                            clone.sendMessage(rd + "Error! You can't promote/demote a clone with the same or greater rank!");
                            return;
                        }
                        if (!rankIsLegit) {
                            clone.sendMessage(rd + "Error! The rank " + wh + rank + rd + " is not a rank!");
                            return;
                        }
                        if (givenRankIsGreaterThanGiversRank) {
                            clone.sendMessage(rd + "Error! The rank " + wh + rank + rd + " is greater than your own rank, you cannot do this!");
                            return;
                        }

                        cloneHandler.cloneRankup(targetCloneName, rank);
                    }
                });
            }
        });
    }
}
