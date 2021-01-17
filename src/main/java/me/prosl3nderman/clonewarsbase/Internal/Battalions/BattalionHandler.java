package me.prosl3nderman.clonewarsbase.Internal.Battalions;

import com.google.inject.Injector;
import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.APIs.SkinAPI;
import me.prosl3nderman.clonewarsbase.Internal.Handler;
import me.prosl3nderman.clonewarsbase.Internal.APIs.LuckPermsAPI;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Configs.Config;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Configs.ConfigHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Singleton
public class BattalionHandler implements Handler {

    private Injector injector;
    private CloneWarsBase plugin;
    private ConfigHandler configHandler;
    private MySQLDatabase mySQLDatabase;
    private SkinAPI skinAPI;

    @Inject
    public BattalionHandler(Injector injector, CloneWarsBase plugin, ConfigHandler configHandler, MySQLDatabase mySQLDatabase, SkinAPI skinAPI) {
        this.injector = injector;
        this.plugin = plugin;
        this.configHandler = configHandler;
        this.mySQLDatabase = mySQLDatabase;
        this.skinAPI = skinAPI;
    }

    private HashMap<String, Battalion> battalions = new HashMap<>();

    @Override
    public void enable() {
        ensureDefaultBattalionFilesAreSavedInServerFiles();
        goThroughBattalionDirectoryAndLoadBattalions(new File(plugin.getDataFolder() + File.separator + "battalions"), false, false);
    }

    public void goThroughBattalionDirectoryAndLoadBattalions(File dir, Boolean loadOnlySkins, Boolean reloadAllSkins) {
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().contains("config.yml"))
                continue;
            if (file.isDirectory())
                goThroughBattalionDirectoryAndLoadBattalions(file, loadOnlySkins, reloadAllSkins);
            else {
                if (file.getName().contains(".yml") && !loadOnlySkins) {
                    String battalionName = file.getName().replace(".yml", "");
                    loadBattalion(battalionName);
                } else if (file.getName().contains(".png"))
                        skinAPI.loadSkin(file.getParentFile().getName(), file.getName().replace(".png", ""), file, reloadAllSkins);
            }
        }
    }

    private void ensureDefaultBattalionFilesAreSavedInServerFiles() {
        plugin.saveResource("battalions" + File.separator + "cr" + File.separator + "cr.yml", false);
        plugin.saveResource("battalions" + File.separator + "cr" + File.separator + "cr.png", false);
        plugin.saveResource("battalions" + File.separator + "ct" + File.separator + "ct.yml", false);
        plugin.saveResource("battalions" + File.separator + "ct" + File.separator + "ct.png", false);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                mySQLDatabase.createBattalion("cr");
                mySQLDatabase.createBattalion("ct");
            }
        }, 40L);
    }

    @Override
    public void disable() {
        battalions.clear();
    }

    private Battalion loadBattalion(String battalionName) {
        if (battalions.containsKey(battalionName))
            battalions.remove(battalionName);

        Battalion battalion = injector.getInstance(Battalion.class);
        battalion.loadVariables(battalionName);
        battalions.put(battalionName, battalion);
        return battalion;
    }
    
    public Battalion getBattalion(String battalionName) {
        if (battalions.containsKey(battalionName))
            return battalions.get(battalionName);
        return null;
    }

    public void reloadAllBattalionVariables() {
        for (Battalion battalion : battalions.values())
            battalion.loadVariables(battalion.getName());
    }

    public void createBattalion(Player player, String battalionName) {
        if (battalions.containsKey(battalionName)) {
            player.sendMessage(ChatColor.RED + "The battalion " + ChatColor.WHITE + battalionName + ChatColor.RED
                    + " already exists! To delete it, try " + ChatColor.WHITE + "/CWB delete <battalion>"
                    + ChatColor.RED + ".");
            return;
        }
        Config battConfig = configHandler.loadConfig(battalionName, "battalions/" + battalionName);
        battConfig.replicateOtherConfig("battalionExample.yml");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                mySQLDatabase.createBattalion(battalionName);
            }
        });

        loadBattalion(battalionName);

        player.sendMessage(ChatColor.GREEN + "The battalion " + ChatColor.WHITE + battalionName + ChatColor.GREEN
                + " has been created!");
    }

    private List<UUID> deleteBattalionConfirmation = new ArrayList<>();

    public void deleteBattalion(Player player, String battalionName) {
        if (!battalions.containsKey(battalionName)) {
            player.sendMessage(ChatColor.RED + "The battalion " + ChatColor.WHITE + battalionName + ChatColor.RED
                    + " does not exist!");
            return;
        }
        if (!deleteBattalionConfirmation.contains(player.getUniqueId())) {
            deleteBattalionConfirmation.add(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "WARNING WARNING WARNING WARNING WARNING: ");
            player.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "This is not reversable! If you are sure about this," +
                    "type the command again.");
            return;
        }
        deleteBattalionConfirmation.remove(player.getUniqueId());

        configHandler.getConfig(battalionName).delete();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                mySQLDatabase.deleteBattalion(battalionName);
            }
        });

        battalions.remove(battalionName);

        player.sendMessage(ChatColor.GREEN + "The battalion " + ChatColor.WHITE + battalionName + ChatColor.GREEN
                + " was deleted!");
    }
}
