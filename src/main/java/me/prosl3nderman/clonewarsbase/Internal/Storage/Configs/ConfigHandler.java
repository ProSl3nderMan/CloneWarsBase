package me.prosl3nderman.clonewarsbase.Internal.Storage.Configs;

import com.google.inject.Injector;
import com.google.inject.Provider;
import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import me.prosl3nderman.clonewarsbase.Internal.Battalions.BattalionHandler;
import me.prosl3nderman.clonewarsbase.Internal.Handler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.HashMap;

@Singleton
public class ConfigHandler implements Handler {

    private Injector injector;
    private CloneWarsBase plugin;
    private Provider<BattalionHandler> battalionHandlerProvider;

    @Inject
    public ConfigHandler(Injector injector, CloneWarsBase plugin, Provider<BattalionHandler> battalionHandlerProvider) {
        this.injector = injector;
        this.plugin = plugin;
        this.battalionHandlerProvider = battalionHandlerProvider;
    }

    private HashMap<String, Config> loadedConfigs = new HashMap<>();
    private String defaultDir;

    @Override
    public void enable() {
        defaultDir = plugin.getDataFolder() + File.separator;
        goThroughAllPluginDirectoriesAndLoadTheConfigs(new File(defaultDir));
    }

    private void goThroughAllPluginDirectoriesAndLoadTheConfigs(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().contains("config.yml"))
                continue;
            if (file.isDirectory())
                goThroughAllPluginDirectoriesAndLoadTheConfigs(file);
            else {
                if (file.getName().contains(".yml")) {
                    String configName = file.getName().replace(".yml", "");
                    loadConfig(configName, getProperDirFromAbsolutePath(file.getAbsolutePath())).reloadConfig();
                }
            }
        }
    }

    private String getProperDirFromAbsolutePath(String currentDir) {
        currentDir = currentDir.replaceFirst("/", "");
        return currentDir;
    }

    @Override
    public void disable() {
        for (Config config : loadedConfigs.values())
            config.saveConfig();
        loadedConfigs.clear();
    }

    public void loadConfig(String configName) {
        loadConfig(configName, defaultDir);
    }

    public Config loadConfig(String configName, String dir) {
        if (loadedConfigs.containsKey(configName))
            return loadedConfigs.get(configName);
        dir = getProperDir(dir);
        Config config = injector.getInstance(Config.class);
        config.setupConfig(configName, dir);
        config.reloadConfig();

        loadedConfigs.put(configName, config);
        return config;
    }

    private String getProperDir(String dir) {
        if (!dir.equalsIgnoreCase(defaultDir) && !dir.contains(defaultDir)) {
            dir = defaultDir + dir;
            if (!dir.endsWith(File.separator))
                dir += File.separator;
        } else {
            String newDirBuilder = "null";
            for (String dirPart : dir.split(File.separator)) {
                if ((newDirBuilder.equalsIgnoreCase("null") && dirPart.equalsIgnoreCase("plugins"))
                        || (!newDirBuilder.equalsIgnoreCase("null") && !dirPart.contains(".yml")))
                    newDirBuilder = newDirBuilder.equalsIgnoreCase("null") ? dirPart + File.separator
                            : newDirBuilder + dirPart + File.separator;
            }
            dir = newDirBuilder;
        }
        return dir;
    }

    public Config getConfig(String configName) {
        if (loadedConfigs.containsKey(configName))
            return loadedConfigs.get(configName);
        return getConfig(configName, defaultDir);
    }

    public Config getConfig(String configName, String dir) {
        return loadConfig(configName, dir);
    }

    public void reloadAllConfigs() {
        goThroughAllPluginDirectoriesAndLoadTheConfigs(new File(defaultDir));

        battalionHandlerProvider.get().reloadAllBattalionVariables();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }
}
