package me.prosl3nderman.clonewarsbase.Internal.Storage.Configs;

import me.prosl3nderman.clonewarsbase.CloneWarsBase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.inject.Inject;
import java.io.*;
import java.util.logging.Level;

public class Config {

    private CloneWarsBase plugin;
    private FileConfiguration customConfig = null;
    private File customConfigFile = null;
    private String dir;
    private String configName;

    @Inject
    public Config(CloneWarsBase plugin) {
        this.plugin = plugin;
    }

    public void setupConfig(String configName, String dir) {
        this.configName = configName;
        this.dir = dir;

        File directory = new File(dir);
        if (!directory.exists())
            directory.mkdir();
    }

    public void reloadConfig() {
        if (customConfigFile == null) {

            customConfigFile = new File(dir, configName + ".yml");
        }
        if (!customConfigFile.exists()) {
            try {
                customConfigFile.createNewFile();
                plugin.getLogger().log(Level.INFO, "The config file " + configName + " has been created at " + customConfigFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public FileConfiguration getConfig() {
        if (customConfig == null) {
            reloadConfig();
        }
        return customConfig;
    }

    public void saveConfig() {
        if (customConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getConfig().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    public void srConfig() {
        saveConfig();
        reloadConfig();
    }

    public void delete() {
        if (customConfig == null)
            customConfigFile = new File(dir, configName + ".yml");
        if (customConfigFile.exists()) {
            customConfigFile.delete();
            Bukkit.getLogger().log(Level.INFO, configName + ".yml has been deleted.");
        } else
            Bukkit.getLogger().log(Level.WARNING, configName + ".yml, skipping deletion.");
    }

    public void replicateOtherConfig(String otherConfig) {
        if (!otherConfig.contains(".yml"))
            otherConfig += ".yml";

        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(plugin.getResource(otherConfig), "UTF-8");
            try {
                customConfig.load(defConfigStream);
                customConfig.save(customConfigFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
