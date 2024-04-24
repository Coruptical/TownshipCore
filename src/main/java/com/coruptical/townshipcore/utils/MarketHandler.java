package com.coruptical.townshipcore.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.models.MarketItem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MarketHandler {
    // Make class read-only
    private MarketHandler(){

    };

    private static MarketHandler instance = new MarketHandler();
    private TownshipCore plugin;

    public static MarketHandler getInstance() {
        return instance;
    }

    public void init(TownshipCore pluginInstance){
        this.plugin = pluginInstance;
    }

    // Rest of driver code

    private File customConfigFile;
    private FileConfiguration customConfig;
    private final String configFileName = "market_data.yml";

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    public void createCustomConfig() {
        customConfigFile = new File(plugin.getDataFolder(), configFileName);
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            plugin.saveResource(configFileName, false);
         }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        // YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public HashMap<String, MarketItem> getMarketItems(){
        HashMap<String, MarketItem> marketItemsMap = new HashMap<>();

        FileConfiguration config = getCustomConfig();
        List<String> itemPathNames = config.getStringList("items");
        plugin.getLogger().log(Level.INFO, itemPathNames.toString());
        return marketItemsMap;
    }
}
