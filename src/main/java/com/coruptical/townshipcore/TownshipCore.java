package com.coruptical.townshipcore;

import com.coruptical.townshipcore.economy.EconomyHandler;
import com.coruptical.townshipcore.models.MarketItem;
import com.coruptical.townshipcore.utils.DatabaseHandler;
import com.coruptical.townshipcore.utils.MarketHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

public final class TownshipCore extends JavaPlugin {
    private static TownshipCore instance;
    private HashMap<String, MarketItem> marketItemMap = new HashMap<>();
    private MarketHandler marketHandler;
    private DatabaseHandler databaseHandler;
    private EconomyHandler economyHandler;

    public HashMap<String, MarketItem> getMarketItemMap() {
        return marketItemMap;
    }

    public DatabaseHandler getDatabaseHandler () {
        return databaseHandler;
    }

    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }

    public static TownshipCore getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Set up database
        databaseHandler = DatabaseHandler.getInstance();
        databaseHandler.init(this);

        // Set up database tables and create economy
        economyHandler = EconomyHandler.getInstance();
        economyHandler.init(this, getDatabaseHandler());

        economyHandler.createTable();

        // Set up market handler and market items
        marketHandler = MarketHandler.getInstance();
        marketHandler.init(this);
        marketHandler.createCustomConfig();

        marketItemMap = marketHandler.getMarketItems();

        for (String key : marketItemMap.keySet()){
            MarketItem item = marketItemMap.get(key);
            getLogger().log(Level.INFO, String.format(
                    "%s: %s",
                    key, item
            ));
        }
    }

    @Override
    public void onDisable() {

    }

    public MarketHandler getMarketHandler(){
        return marketHandler;
    }

}
