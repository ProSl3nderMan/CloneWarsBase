package me.prosl3nderman.clonewarsbase;

import com.google.inject.Injector;
import me.prosl3nderman.clonewarsbase.Events.PlayerJoinServerEvent;
import me.prosl3nderman.clonewarsbase.Events.PlayerLeaveServerEvent;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.Commands.CWBCommand;
import me.prosl3nderman.clonewarsbase.Internal.Handler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.ConfigHandler;
import me.prosl3nderman.clonewarsbase.Internal.Player.PlayerHandler;
import me.prosl3nderman.clonewarsbase.Internal.Storage.Database.MySQLDatabase;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CloneWarsBase extends JavaPlugin {

    private LuckPerms lpAPI = null;

    private Injector injector;

    @Inject
    private PlayerHandler playerHandler;
    @Inject
    private ConfigHandler configHandler;
    @Inject
    private BattalionHandler battalionHandler;
    @Inject
    private MySQLDatabase mySQLDatabase;

    private List<Handler> allHandlers = new ArrayList<>();

    @Override
    public void onEnable() {
        // setup luck perms before Guice Modules because luck perms is injected into the module.
        setupLuckPermsAPI();

        // register GUICE and bind all instances to the appropriate instance.
        registerGuiceModules();

        // register all commands
        registerCommands();

        // register all listeners
        registerListeners();

        // ensure default config is already created, and reload it for any offline changes
        doConfig();

        // load default values for mysql database - has to be done after doConfig() since it gets values from there.
        loadDefaultValuesMySQLDatabase();

        // enable all handlers
        enableHandlers();

        // if any online players, onJoin them properly. (this is if the plugin was disabled and re-enabled without server restart)
        checkForOnlinePlayersAndJoinThem();
    }

    @Override
    public void onDisable() {
        // disable all handlers
        disableHandlers();
    }

    private void setupLuckPermsAPI() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
            lpAPI = provider.getProvider();
    }

    private void registerGuiceModules() {
        CloneWarsBaseModule module = new CloneWarsBaseModule(this, lpAPI);
        injector = module.createInjector();
        injector.injectMembers(this);
    }

    private void registerCommands() {
        getCommand("CWB").setExecutor(injector.getInstance(CWBCommand.class));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(injector.getInstance(PlayerJoinServerEvent.class), this);
        getServer().getPluginManager().registerEvents(injector.getInstance(PlayerLeaveServerEvent.class), this);
    }

    private void enableHandlers() {
        allHandlers.addAll(Arrays.asList(playerHandler, configHandler, battalionHandler));
        for (Handler handler : allHandlers)
            handler.enable();
    }

    private void checkForOnlinePlayersAndJoinThem() {
        if (Bukkit.getOnlinePlayers().size() != 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers())
                        playerHandler.loadPlayer(player);
                }
            }, 40L);
        }
    }

    private void disableHandlers() {
        for (Handler handler : allHandlers)
            handler.disable();
    }

    private void doConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void loadDefaultValuesMySQLDatabase() {
        mySQLDatabase.loadDefaultValues();
    }

    public void srConfig() {
        saveConfig();
        reloadConfig();
    }
}
