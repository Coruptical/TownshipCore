package com.coruptical.townshipcore.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.coruptical.townshipcore.TownshipCore;
import com.coruptical.townshipcore.models.MarketItem;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MarketHandler {
    // Make class read-only
    private MarketHandler(){

    };

    private static MarketHandler instance = new MarketHandler();
    private TownshipCore plugin;

    private HashMap<String, MarketItem> marketItemMap = new HashMap<>();

    public HashMap<String, MarketItem> getMarketItemMap() {
        return marketItemMap;
    }

    public static MarketHandler getInstance() {
        return instance;
    }

    public void init(TownshipCore pluginInstance){
        this.plugin = pluginInstance;
        marketItemMap = getMarketItems();
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
    }

    public HashMap<String, MarketItem> getMarketItems(){
        HashMap<String, MarketItem> marketItemsMap = new HashMap<>();

        FileConfiguration config = getCustomConfig();
        List<String> itemPathNames = config.getStringList("items");

        for (String itemPath : itemPathNames){
            String itemName = config.getString(String.format(
                    "%s.name",
                    itemPath
            ));

            double itemBuyPrice = Utils.round(config.getDouble(String.format(
                    "%s.buyPrice",
                    itemPath
            )));

            double itemSellPrice = Utils.round(config.getDouble(String.format(
                    "%s.sellPrice",
                    itemPath
            )));

            itemPath = itemPath.toUpperCase();
            Material material = Material.getMaterial(itemPath);
            if (material == null){
                plugin.getLogger().log(Level.WARNING, String.format("Couldn't load market material under %s.", itemPath));
                material = Material.BARRIER;
            }

            MarketItem marketItem = new MarketItem(itemName, itemBuyPrice, itemSellPrice, material);
            marketItemsMap.put(itemPath, marketItem);
        }

        return marketItemsMap;
    }


}
