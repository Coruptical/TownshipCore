package com.coruptical.townshipcore.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MarketHandler {
    private File customConfigFile;
    private FileConfiguration customConfig;
    private final String configFileName = "market_data.yml";

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig(JavaPlugin plugin) {
        customConfigFile = new File(plugin.getDataFolder(), configFileName);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource("custom.yml", false);
         }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        // YamlConfiguration.loadConfiguration(customConfigFile);
    }
}
